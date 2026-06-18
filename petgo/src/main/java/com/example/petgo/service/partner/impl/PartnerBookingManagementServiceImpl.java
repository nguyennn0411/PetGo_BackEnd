package com.example.petgo.service.partner.impl;

import com.example.petgo.dto.BookingMutationResponse;
import com.example.petgo.dto.partner.PartnerBookingActionRequest;
import com.example.petgo.dto.partner.PartnerBookingDetailResponse;
import com.example.petgo.dto.partner.PartnerBookingListResponse;
import com.example.petgo.dto.partner.PartnerInternalNoteRequest;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.*;
import com.example.petgo.service.impl.BookingNotificationService;
import com.example.petgo.service.partner.PartnerAccessService;
import com.example.petgo.service.partner.PartnerBookingManagementService;
import com.example.petgo.service.partner.PartnerMappingSupport;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PartnerBookingManagementServiceImpl implements PartnerBookingManagementService {

    private final PartnerAccessService partnerAccessService;
    private final PartnerMappingSupport mapper;
    private final BookingRepository bookingRepository;
    private final BookingStatusHistoryRepository bookingStatusHistoryRepository;
    private final BookingCancellationRepository bookingCancellationRepository;
    private final ProviderAvailabilitySlotRepository providerAvailabilitySlotRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final BookingNotificationService bookingNotificationService;

    @Override
    @Transactional(readOnly = true)
    public PartnerBookingListResponse listBookings(HttpServletRequest request, String status, String from, String to,
            Long serviceId) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        String normalizedStatus = normalizeStatusFilter(status);
        LocalDate fromDate = parseOptionalDate(from);
        LocalDate toDate = parseOptionalDate(to);
        if (fromDate != null && toDate != null && toDate.isBefore(fromDate)) {
            throw new BadRequestException("Khoảng ngày lọc booking không hợp lệ.");
        }

        List<Booking> allBookings = bookingRepository.findDetailedByProviderId(provider.getId());
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("ALL", (long) allBookings.size());
        counts.put("PENDING", allBookings.stream().filter(booking -> mapper.isPending(booking.getStatus())).count());
        counts.put("CONFIRMED", allBookings.stream().filter(booking -> statusEquals(booking, "CONFIRMED")).count());
        counts.put("IN_PROGRESS", allBookings.stream().filter(booking -> statusEquals(booking, "IN_PROGRESS")).count());
        counts.put("COMPLETED", allBookings.stream().filter(booking -> statusEquals(booking, "COMPLETED")).count());
        counts.put("CANCELLED", allBookings.stream().filter(booking -> statusEquals(booking, "CANCELLED")).count());

        return PartnerBookingListResponse.builder()
                .providerId(provider.getId())
                .filterStatus(normalizedStatus)
                .counts(counts)
                .bookings(allBookings.stream()
                        .filter(booking -> matchesStatus(booking, normalizedStatus))
                        .filter(booking -> matchesDateRange(booking, fromDate, toDate))
                        .filter(booking -> serviceId == null || (booking.getProviderService() != null
                                && Objects.equals(booking.getProviderService().getId(), serviceId)))
                        .map(mapper::mapBookingSummary)
                        .toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerBookingDetailResponse getBookingDetail(HttpServletRequest request, Long bookingId) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        Booking booking = requireOwnedBooking(provider.getId(), bookingId);
        return mapper.mapBookingDetail(booking,
                bookingStatusHistoryRepository.findByBookingIdOrderByCreatedAtAscIdAsc(bookingId));
    }

    @Override
    @Transactional
    public BookingMutationResponse confirmBooking(HttpServletRequest request, Long bookingId,
            PartnerBookingActionRequest requestBody) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        Booking booking = requireOwnedBooking(provider.getId(), bookingId);
        if (!mapper.canConfirm(booking)) {
            throw new BadRequestException("Booking hiện không thể xác nhận.");
        }
        BookingMutationResponse response = transitionBooking(booking, provider.getUser(), "CONFIRMED",
                noteOrDefault(requestBody, "Partner xác nhận booking"));
        bookingNotificationService.notifyOwnerBookingConfirmed(booking);
        return response;
    }

    @Override
    @Transactional
    public BookingMutationResponse startBooking(HttpServletRequest request, Long bookingId,
            PartnerBookingActionRequest requestBody) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        Booking booking = requireOwnedBooking(provider.getId(), bookingId);
        if (!mapper.canStart(booking)) {
            throw new BadRequestException("Booking hiện không thể bắt đầu.");
        }
        return transitionBooking(booking, provider.getUser(), "IN_PROGRESS",
                noteOrDefault(requestBody, "Partner bắt đầu phục vụ"));
    }

    @Override
    @Transactional
    public BookingMutationResponse completeBooking(HttpServletRequest request, Long bookingId,
            PartnerBookingActionRequest requestBody) {
        return confirmCompletedByProvider(request, bookingId, requestBody);
    }

    @Override
    @Transactional
    public BookingMutationResponse rejectBooking(HttpServletRequest request, Long bookingId,
            PartnerBookingActionRequest requestBody) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        Booking booking = requireOwnedBooking(provider.getId(), bookingId);
        String status = mapper.firstNonBlank(booking.getStatus(), "").toUpperCase(Locale.ROOT);
        if (!List.of("PENDING_PROVIDER_CONFIRMATION", "PENDING_CONFIRMATION").contains(status)) {
            throw new BadRequestException("Booking hiện không thể từ chối.");
        }
        String reason = mapper.normalizeBlank(requestBody != null ? requestBody.reasonText() : null);
        refundBookingEscrow(booking, "Provider từ chối booking" + (reason != null ? ": " + reason : ""));
        BookingMutationResponse response = transitionBooking(booking, provider.getUser(), "REJECTED",
                noteOrDefault(requestBody, "Provider từ chối booking, hoàn tiền về ví user"));
        bookingNotificationService.notifyOwnerBookingRejected(booking, reason);
        return BookingMutationResponse.builder()
                .bookingId(response.bookingId())
                .bookingCode(response.bookingCode())
                .status(response.status())
                .statusLabel(response.statusLabel())
                .appointmentDate(response.appointmentDate())
                .appointmentDateDisplay(response.appointmentDateDisplay())
                .appointmentTime(response.appointmentTime())
                .slotId(response.slotId())
                .message("Đã từ chối booking và hoàn tiền về ví user.")
                .refundAmount(mapper.defaultMoney(booking.getTotalAmount()))
                .refundAmountDisplay(mapper.formatMoney(booking.getTotalAmount()))
                .refundStatus("REFUNDED_TO_WALLET")
                .build();
    }

    @Override
    @Transactional
    public BookingMutationResponse confirmCompletedByProvider(HttpServletRequest request, Long bookingId,
            PartnerBookingActionRequest requestBody) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        Booking booking = requireOwnedBooking(provider.getId(), bookingId);
        if (!mapper.canComplete(booking)) {
            throw new BadRequestException("Booking hiện không thể xác nhận hoàn tất.");
        }
        String current = mapper.firstNonBlank(booking.getStatus(), "").toUpperCase(Locale.ROOT);
        String nextStatus = "COMPLETED_BY_USER".equals(current) ? "COMPLETED" : "COMPLETED_BY_PROVIDER";
        BookingMutationResponse response = transitionBooking(booking, provider.getUser(), nextStatus,
                noteOrDefault(requestBody, "Provider xác nhận hoàn tất dịch vụ"));
        if ("COMPLETED".equals(nextStatus)) {
            provider.setTotalCompletedBookings(
                    Math.max(0, provider.getTotalCompletedBookings() == null ? 0 : provider.getTotalCompletedBookings())
                            + 1);
            // Giải ngân escrow ngay khi booking hoàn tất
            releaseBookingEscrowIfCompleted(booking);
        }
        return response;
    }

    @Override
    @Transactional
    public BookingMutationResponse cancelBooking(HttpServletRequest request, Long bookingId,
            PartnerBookingActionRequest requestBody) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        Booking booking = requireOwnedBooking(provider.getId(), bookingId);
        if (!mapper.canPartnerCancel(booking)) {
            throw new BadRequestException("Booking hiện không thể hủy.");
        }
        String reasonCode = mapper.firstNonBlank(requestBody != null ? requestBody.reasonCode() : null,
                "PARTNER_CANCELLED");
        String reasonText = mapper.normalizeBlank(requestBody != null ? requestBody.reasonText() : null);
        if (reasonText == null || reasonText.length() < 5) {
            throw new BadRequestException("Vui lòng nhập lý do hủy booking tối thiểu 5 ký tự.");
        }
        String previousStatus = booking.getStatus();
        if (booking.getAvailabilitySlot() != null) {
            releaseSlot(booking.getAvailabilitySlot());
        }
        booking.setStatus("CANCELLED");
        booking.setCancellationReasonCode(reasonCode);
        bookingRepository.save(booking);

        if (bookingCancellationRepository.findByBookingId(bookingId).isEmpty()) {
            BookingCancellation cancellation = new BookingCancellation();
            cancellation.setBooking(booking);
            cancellation.setCancelledByUser(provider.getUser());
            cancellation.setReasonCode(reasonCode);
            cancellation.setReasonText(reasonText);
            cancellation.setRefundStatus("REVIEW_REQUIRED");
            cancellation.setRefundAmount(BigDecimal.ZERO);
            bookingCancellationRepository.save(cancellation);
        }

        saveHistory(booking, previousStatus, "CANCELLED", provider.getUser(),
                "Partner hủy booking" + (reasonText != null ? ": " + reasonText : ""));
        return buildMutationResponse(booking, "Đã hủy booking.");
    }

    @Override
    @Transactional
    public PartnerBookingDetailResponse updateInternalNote(HttpServletRequest request, Long bookingId,
            PartnerInternalNoteRequest requestBody) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        Booking booking = requireOwnedBooking(provider.getId(), bookingId);
        booking.setInternalNote(mapper.normalizeBlank(requestBody.internalNote()));
        bookingRepository.save(booking);
        saveHistory(booking, booking.getStatus(), booking.getStatus(), provider.getUser(),
                "Partner cập nhật ghi chú nội bộ");
        return mapper.mapBookingDetail(booking,
                bookingStatusHistoryRepository.findByBookingIdOrderByCreatedAtAscIdAsc(bookingId));
    }

    private Booking requireOwnedBooking(Long providerId, Long bookingId) {
        return bookingRepository.findDetailedByProviderIdAndBookingId(providerId, bookingId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Không tìm thấy booking thuộc nhà cung cấp hiện tại."));
    }

    private BookingMutationResponse transitionBooking(Booking booking, User changedBy, String nextStatus, String note) {
        String previousStatus = booking.getStatus();
        booking.setStatus(nextStatus);
        bookingRepository.save(booking);
        saveHistory(booking, previousStatus, nextStatus, changedBy, note);
        return buildMutationResponse(booking, "Cập nhật booking thành công.");
    }

    private void saveHistory(Booking booking, String previousStatus, String nextStatus, User changedBy, String note) {
        BookingStatusHistory history = new BookingStatusHistory();
        history.setBooking(booking);
        history.setFromStatus(previousStatus);
        history.setToStatus(nextStatus);
        history.setChangedByUser(changedBy);
        history.setNote(note);
        bookingStatusHistoryRepository.save(history);
    }

    private BookingMutationResponse buildMutationResponse(Booking booking, String message) {
        return BookingMutationResponse.builder()
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .status(booking.getStatus())
                .statusLabel(mapper.mapStatusLabel(booking.getStatus()))
                .appointmentDate(mapper.formatIsoDate(booking.getAppointmentDate()))
                .appointmentDateDisplay(mapper.formatDate(booking.getAppointmentDate()))
                .appointmentTime(mapper.formatTimeRange(booking.getStartTime(), booking.getEndTime()))
                .slotId(booking.getAvailabilitySlot() != null ? booking.getAvailabilitySlot().getId() : null)
                .message(message)
                .refundAmount(BigDecimal.ZERO)
                .refundAmountDisplay(mapper.formatMoney(BigDecimal.ZERO))
                .refundStatus(null)
                .build();
    }

    private void refundBookingEscrow(Booking booking, String note) {
        User owner = booking.getCustomerUser();
        if (owner == null) {
            throw new BadRequestException("Booking thiếu thông tin user để hoàn tiền.");
        }
        WalletTransaction holdTx = walletTransactionRepository
                .findFirstWithLockByGatewayTransactionIdAndTypeAndStatusOrderByCreatedAtDescIdDesc(
                        "BOOKING:" + booking.getId(), "BOOKING_ESCROW_HOLD", "HELD_BY_ADMIN")
                .orElseThrow(() -> new BadRequestException("Không tìm thấy khoản escrow đang giữ của booking."));
        Wallet wallet = walletRepository.findWithLockByUserId(owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Ví user chưa được khởi tạo."));
        BigDecimal amount = mapper.defaultMoney(holdTx.getAmount());
        BigDecimal before = mapper.defaultMoney(wallet.getBalance());
        wallet.setBalance(before.add(amount));
        walletRepository.save(wallet);

        holdTx.setStatus("REFUNDED_TO_USER");
        holdTx.setReviewNote(note);
        walletTransactionRepository.save(holdTx);

        WalletTransaction refundTx = new WalletTransaction();
        refundTx.setTransactionCode("BOOKINGREFUND-" + java.util.UUID.randomUUID().toString().replace("-", "")
                .substring(0, 12).toUpperCase(Locale.ROOT));
        refundTx.setWallet(wallet);
        refundTx.setUser(owner);
        refundTx.setType("BOOKING_REFUND");
        refundTx.setStatus("COMPLETED");
        refundTx.setAmount(amount);
        refundTx.setGatewayTransactionId("BOOKING:" + booking.getId() + ":REFUND");
        refundTx.setBalanceBefore(before);
        refundTx.setBalanceAfter(wallet.getBalance());
        refundTx.setNote(note);
        walletTransactionRepository.save(refundTx);
    }

    private void releaseBookingEscrowIfCompleted(Booking booking) {
        if (booking == null || booking.getProvider() == null || booking.getProvider().getUser() == null) {
            return;
        }
        try {
            WalletTransaction holdTx = walletTransactionRepository
                    .findFirstWithLockByGatewayTransactionIdAndTypeAndStatusOrderByCreatedAtDescIdDesc(
                            "BOOKING:" + booking.getId(), "BOOKING_ESCROW_HOLD", "HELD_BY_ADMIN")
                    .orElse(null);
            if (holdTx == null) {
                return;
            }
            // Cộng tiền vào ví provider
            Wallet providerWallet = getOrCreateWalletWithLock(booking.getProvider().getUser());
            BigDecimal before = mapper.defaultMoney(providerWallet.getBalance());
            providerWallet.setBalance(before.add(holdTx.getAmount()));
            walletRepository.save(providerWallet);

            // Tạo transaction credit cho provider
            WalletTransaction creditTx = new WalletTransaction();
            creditTx.setTransactionCode(generateWalletTransactionCode("BOOKING_RELEASE"));
            creditTx.setWallet(providerWallet);
            creditTx.setUser(booking.getProvider().getUser());
            creditTx.setType("BOOKING_ESCROW_RELEASE");
            creditTx.setStatus("COMPLETED");
            creditTx.setAmount(holdTx.getAmount());
            creditTx.setGatewayTransactionId("BOOKING:" + booking.getId());
            creditTx.setBalanceBefore(before);
            creditTx.setBalanceAfter(providerWallet.getBalance());
            creditTx.setNote("Giải ngân escrow booking " + booking.getBookingCode() + " sau khi hoàn tất.");
            walletTransactionRepository.save(creditTx);

            // Cập nhật status hold transaction
            holdTx.setStatus("RELEASED_TO_PROVIDER");
            holdTx.setReviewNote("Auto released to provider when booking completed.");
            walletTransactionRepository.save(holdTx);
        } catch (Exception e) {
            // Log error nhưng không fail booking completion
            System.err.println("Lỗi giải ngân escrow booking " + booking.getId() + ": " + e.getMessage());
        }
    }

    private Wallet getOrCreateWalletWithLock(User owner) {
        walletRepository.findByUserId(owner.getId()).orElseGet(() -> {
            Wallet wallet = new Wallet();
            wallet.setUser(owner);
            return walletRepository.save(wallet);
        });
        return walletRepository.findWithLockByUserId(owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Ví chưa được khởi tạo."));
    }

    private String generateWalletTransactionCode(String prefix) {
        return prefix.replace("_", "") + "-"
                + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12)
                .toUpperCase(Locale.ROOT);
    }

    private void releaseSlot(ProviderAvailabilitySlot slot) {
        int booked = Math.max(0, (slot.getCapacityBooked() == null ? 0 : slot.getCapacityBooked()) - 1);
        slot.setCapacityBooked(booked);
        if (!"UNAVAILABLE".equalsIgnoreCase(mapper.firstNonBlank(slot.getSlotStatus(), "AVAILABLE"))) {
            slot.setSlotStatus("AVAILABLE");
        }
        providerAvailabilitySlotRepository.save(slot);
    }

    private String noteOrDefault(PartnerBookingActionRequest requestBody, String defaultNote) {
        return mapper.firstNonBlank(requestBody != null ? requestBody.note() : null, defaultNote);
    }

    private String normalizeStatusFilter(String status) {
        if (status == null || status.isBlank())
            return "ALL";
        return status.trim().toUpperCase(Locale.ROOT);
    }

    private boolean matchesStatus(Booking booking, String filter) {
        return switch (filter) {
            case "PENDING" -> mapper.isPending(booking.getStatus());
            case "CONFIRMED", "IN_PROGRESS", "COMPLETED", "CANCELLED" -> statusEquals(booking, filter);
            default -> true;
        };
    }

    private boolean statusEquals(Booking booking, String expected) {
        return expected.equalsIgnoreCase(mapper.firstNonBlank(booking.getStatus(), ""));
    }

    private boolean matchesDateRange(Booking booking, LocalDate fromDate, LocalDate toDate) {
        if (booking.getAppointmentDate() == null)
            return true;
        if (fromDate != null && booking.getAppointmentDate().isBefore(fromDate))
            return false;
        return toDate == null || !booking.getAppointmentDate().isAfter(toDate);
    }

    private LocalDate parseOptionalDate(String value) {
        String normalized = mapper.normalizeBlank(value);
        if (normalized == null)
            return null;
        try {
            return LocalDate.parse(normalized, PartnerMappingSupport.ISO_DATE);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Ngày filter cần định dạng yyyy-MM-dd.");
        }
    }
}