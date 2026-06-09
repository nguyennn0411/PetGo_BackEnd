package com.example.petgo.service.impl;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.dto.AdminBookingDisputeResponse;
import com.example.petgo.dto.AdminDisputeResolveRequest;
import com.example.petgo.dto.BookingMutationResponse;
import com.example.petgo.dto.ChatConversationResponse;
import com.example.petgo.entity.Booking;
import com.example.petgo.entity.BookingStatusHistory;
import com.example.petgo.entity.User;
import com.example.petgo.entity.Wallet;
import com.example.petgo.entity.WalletTransaction;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.BookingRepository;
import com.example.petgo.repository.BookingStatusHistoryRepository;
import com.example.petgo.repository.UserRepository;
import com.example.petgo.repository.WalletRepository;
import com.example.petgo.repository.WalletTransactionRepository;
import com.example.petgo.service.AdminBookingDisputeService;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminBookingDisputeServiceImpl implements AdminBookingDisputeService {
        private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

        private final AuthService authService;
        private final UserRepository userRepository;
        private final BookingRepository bookingRepository;
        private final BookingStatusHistoryRepository bookingStatusHistoryRepository;
        private final WalletRepository walletRepository;
        private final WalletTransactionRepository walletTransactionRepository;
        private final ChatService chatService;
        private final BookingNotificationService bookingNotificationService;

        @Override
        @Transactional(readOnly = true)
        public List<AdminBookingDisputeResponse> listDisputes(HttpServletRequest request) {
                requireAdmin(request);
                List<AdminBookingDisputeResponse> disputed = bookingRepository
                                .findByStatusOrderByUpdatedAtAscIdAsc("DISPUTED").stream()
                                .map(this::mapDispute)
                                .toList();
                List<AdminBookingDisputeResponse> adminReview = bookingRepository
                                .findByStatusOrderByUpdatedAtAscIdAsc("ADMIN_REVIEW").stream()
                                .map(this::mapDispute)
                                .toList();
                return java.util.stream.Stream.concat(disputed.stream(), adminReview.stream()).toList();
        }

        @Override
        @Transactional
        public BookingMutationResponse resolveDispute(HttpServletRequest request, Long bookingId,
                        AdminDisputeResolveRequest resolveRequest) {
                User admin = requireAdmin(request);
                Booking booking = bookingRepository.findDetailedById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking."));
                chatService.ensureAdminBookingDisputeChat(booking.getId());
                String currentStatus = Optional.ofNullable(booking.getStatus()).orElse("").toUpperCase(Locale.ROOT);
                if (!List.of("DISPUTED", "ADMIN_REVIEW").contains(currentStatus)) {
                        throw new BadRequestException("Booking không ở trạng thái admin có thể xử lý.");
                }

                BigDecimal refund = money(resolveRequest.refundToUserAmount());
                BigDecimal release = money(resolveRequest.releaseToProviderAmount());
                WalletTransaction holdTx = walletTransactionRepository
                                .findFirstByGatewayTransactionIdAndTypeAndStatusOrderByCreatedAtDescIdDesc(
                                                "BOOKING:" + booking.getId(), "BOOKING_ESCROW_HOLD", "HELD_BY_ADMIN")
                                .orElseThrow(() -> new BadRequestException(
                                                "Không tìm thấy khoản escrow đang giữ của booking."));
                BigDecimal escrow = money(holdTx.getAmount());
                if (refund.add(release).compareTo(escrow) > 0) {
                        throw new BadRequestException("Tổng phân bổ không được vượt quá khoản escrow đang giữ.");
                }

                if (refund.compareTo(BigDecimal.ZERO) > 0) {
                        creditWallet(booking.getCustomerUser(), refund, booking, "BOOKING_DISPUTE_REFUND",
                                        "Admin hoàn tiền dispute booking " + booking.getBookingCode());
                }
                if (release.compareTo(BigDecimal.ZERO) > 0) {
                        if (booking.getProvider() == null || booking.getProvider().getUser() == null) {
                                throw new BadRequestException("Provider của booking chưa có user ví hợp lệ.");
                        }
                        creditWallet(booking.getProvider().getUser(), release, booking, "BOOKING_DISPUTE_RELEASE",
                                        "Admin giải ngân dispute booking " + booking.getBookingCode());
                }

                holdTx.setStatus("RESOLVED");
                holdTx.setReviewedByAdmin(admin);
                holdTx.setReviewNote("Dispute resolved. Refund user=" + refund + ", release provider=" + release + ". "
                                + Optional.ofNullable(resolveRequest.reason()).orElse(""));
                walletTransactionRepository.save(holdTx);

                String previous = booking.getStatus();
                booking.setStatus(release.compareTo(BigDecimal.ZERO) > 0 ? "COMPLETED" : "CANCELLED");
                booking.setInternalNote(abbreviate(Optional.ofNullable(booking.getInternalNote()).orElse("")
                                + "\n[ADMIN_DISPUTE_RESOLUTION] refund=" + refund + ", release=" + release + ", reason="
                                + Optional.ofNullable(resolveRequest.reason()).orElse(""), 2000));
                bookingRepository.save(booking);

                BookingStatusHistory history = new BookingStatusHistory();
                history.setBooking(booking);
                history.setFromStatus(previous);
                history.setToStatus(booking.getStatus());
                history.setChangedByUser(admin);
                history.setNote("Admin xử lý dispute: hoàn user " + formatMoney(refund) + ", chuyển provider "
                                + formatMoney(release) + ". "
                                + Optional.ofNullable(resolveRequest.reason()).orElse(""));
                bookingStatusHistoryRepository.save(history);
                bookingNotificationService.notifyDisputeResolved(booking,
                                "Hoàn user " + formatMoney(refund) + ", chuyển provider " + formatMoney(release) + ". "
                                                + Optional.ofNullable(resolveRequest.reason()).orElse(""));

                return BookingMutationResponse.builder()
                                .bookingId(booking.getId())
                                .bookingCode(booking.getBookingCode())
                                .status(booking.getStatus())
                                .statusLabel("COMPLETED".equals(booking.getStatus()) ? "Hoàn thành" : "Đã hủy")
                                .appointmentDate(
                                                booking.getAppointmentDate() != null
                                                                ? booking.getAppointmentDate().format(ISO_DATE)
                                                                : null)
                                .appointmentDateDisplay(booking.getAppointmentDate() != null
                                                ? booking.getAppointmentDate()
                                                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                                : null)
                                .appointmentTime(formatTimeRange(booking))
                                .message("Đã xử lý dispute/admin review booking.")
                                .refundAmount(refund)
                                .refundAmountDisplay(formatMoney(refund))
                                .refundStatus("RESOLVED")
                                .build();
        }

        @Override
        @Transactional
        public ChatConversationResponse openDisputeChat(HttpServletRequest request, Long bookingId) {
                requireAdmin(request);
                Booking booking = bookingRepository.findDetailedById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking."));
                String currentStatus = Optional.ofNullable(booking.getStatus()).orElse("").toUpperCase(Locale.ROOT);
                if (!List.of("DISPUTED", "ADMIN_REVIEW").contains(currentStatus)) {
                        throw new BadRequestException("Chỉ mở chat admin cho booking đang dispute/admin review.");
                }
                return chatService.ensureAdminBookingDisputeChat(booking.getId());
        }

        private User requireAdmin(HttpServletRequest request) {
                AuthenticatedUser current = authService.requireAccessUser(request);
                boolean admin = current.roles() != null && current.roles().stream()
                                .map(role -> role == null ? "" : role.toUpperCase(Locale.ROOT))
                                .anyMatch(role -> role.equals("ADMIN") || role.equals("ROLE_ADMIN"));
                if (!admin) {
                        throw new BadRequestException("Bạn không có quyền admin.");
                }
                return userRepository.findById(current.userId())
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy admin."));
        }

        private void creditWallet(User user, BigDecimal amount, Booking booking, String type, String note) {
                Wallet wallet = getOrCreateWalletWithLock(user);
                BigDecimal before = money(wallet.getBalance());
                wallet.setBalance(before.add(amount));
                walletRepository.save(wallet);

                WalletTransaction tx = new WalletTransaction();
                tx.setTransactionCode(type.replace("_", "") + "-" + UUID.randomUUID().toString().replace("-", "")
                                .substring(0, 12).toUpperCase(Locale.ROOT));
                tx.setWallet(wallet);
                tx.setUser(user);
                tx.setType(type);
                tx.setStatus("COMPLETED");
                tx.setAmount(amount);
                tx.setGatewayTransactionId("BOOKING:" + booking.getId());
                tx.setBalanceBefore(before);
                tx.setBalanceAfter(wallet.getBalance());
                tx.setNote(note);
                walletTransactionRepository.save(tx);
        }

        private Wallet getOrCreateWalletWithLock(User user) {
                walletRepository.findByUserId(user.getId()).orElseGet(() -> {
                        Wallet wallet = new Wallet();
                        wallet.setUser(user);
                        return walletRepository.save(wallet);
                });
                return walletRepository.findWithLockByUserId(user.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Ví chưa được khởi tạo."));
        }

        private AdminBookingDisputeResponse mapDispute(Booking booking) {
                BigDecimal escrow = walletTransactionRepository
                                .findFirstByGatewayTransactionIdAndTypeAndStatusOrderByCreatedAtDescIdDesc(
                                                "BOOKING:" + booking.getId(), "BOOKING_ESCROW_HOLD", "HELD_BY_ADMIN")
                                .map(WalletTransaction::getAmount)
                                .orElse(BigDecimal.ZERO);
                return AdminBookingDisputeResponse.builder()
                                .bookingId(booking.getId())
                                .bookingCode(booking.getBookingCode())
                                .status(booking.getStatus())
                                .statusLabel("ADMIN_REVIEW".equalsIgnoreCase(booking.getStatus()) ? "Chờ admin xử lý"
                                                : "Đang khiếu nại")
                                .customerUserId(booking.getCustomerUser() != null ? booking.getCustomerUser().getId()
                                                : null)
                                .customerName(booking.getCustomerUser() != null
                                                ? booking.getCustomerUser().getFullName()
                                                : null)
                                .providerId(booking.getProvider() != null ? booking.getProvider().getId() : null)
                                .providerName(booking.getProviderNameSnapshot())
                                .serviceName(booking.getServiceNameSnapshot())
                                .appointmentDate(
                                                booking.getAppointmentDate() != null
                                                                ? booking.getAppointmentDate().format(ISO_DATE)
                                                                : null)
                                .appointmentTime(formatTimeRange(booking))
                                .escrowAmount(escrow)
                                .escrowAmountDisplay(formatMoney(escrow))
                                .disputeReason(extractDisputeReason(booking.getInternalNote()))
                                .build();
        }

        private String extractDisputeReason(String note) {
                if (note == null || !note.contains("[USER_DISPUTE]"))
                        return null;
                return note.substring(note.lastIndexOf("[USER_DISPUTE]") + "[USER_DISPUTE]".length()).trim();
        }

        private BigDecimal money(BigDecimal value) {
                return value != null ? value : BigDecimal.ZERO;
        }

        private String formatMoney(BigDecimal amount) {
                return String.format(Locale.forLanguageTag("vi-VN"), "%,.0f đ", money(amount));
        }

        private String formatTimeRange(Booking booking) {
                String start = booking.getStartTime() != null ? booking.getStartTime().format(TIME_FORMATTER) : "";
                String end = booking.getEndTime() != null ? booking.getEndTime().format(TIME_FORMATTER) : "";
                return end.isBlank() ? start : start + " - " + end;
        }

        private String abbreviate(String value, int max) {
                if (value == null)
                        return null;
                String trimmed = value.trim();
                return trimmed.length() <= max ? trimmed : trimmed.substring(0, max);
        }
}