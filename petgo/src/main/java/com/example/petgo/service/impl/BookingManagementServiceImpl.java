package com.example.petgo.service.impl;

import com.example.petgo.dto.*;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.*;
import com.example.petgo.service.BookingManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingManagementServiceImpl implements BookingManagementService {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATE_VIEW = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_VIEW = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter TIME_VIEW = DateTimeFormatter.ofPattern("HH:mm");

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingStatusHistoryRepository bookingStatusHistoryRepository;
    private final BookingCancellationRepository bookingCancellationRepository;
    private final BookingRescheduleRepository bookingRescheduleRepository;
    private final ProviderAvailabilitySlotRepository providerAvailabilitySlotRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional(readOnly = true)
    public BookingListResponse getMyBookings(Long ownerUserId, String status) {
        userRepository.findById(ownerUserId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        String normalizedStatus = normalizeStatusFilter(status);

        List<Booking> bookings = bookingRepository.findDetailedByOwnerUserId(ownerUserId);
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("ALL", (long) bookings.size());
        counts.put("PENDING", bookings.stream().filter(b -> isUiPending(b.getStatus())).count());
        counts.put("CONFIRMED", bookings.stream().filter(b -> "CONFIRMED".equalsIgnoreCase(b.getStatus())).count());
        counts.put("COMPLETED", bookings.stream().filter(b -> "COMPLETED".equalsIgnoreCase(b.getStatus())).count());
        counts.put("CANCELLED", bookings.stream().filter(b -> "CANCELLED".equalsIgnoreCase(b.getStatus())).count());

        List<BookingListItemResponse> items = bookings.stream()
                .filter(booking -> matchesFilter(booking.getStatus(), normalizedStatus))
                .map(this::mapListItem)
                .toList();

        return BookingListResponse.builder()
                .ownerUserId(ownerUserId)
                .filterStatus(normalizedStatus)
                .counts(counts)
                .bookings(items)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDetailResponse getBookingDetail(Long ownerUserId, Long bookingId) {
        Booking booking = getOwnedBooking(ownerUserId, bookingId);
        Invoice invoice = invoiceRepository.findByBookingId(bookingId).orElse(null);
        Payment payment = invoice != null ? paymentRepository.findTopByInvoiceIdOrderByCreatedAtDesc(invoice.getId()).orElse(null) : null;
        List<BookingTimelineItemResponse> timeline = bookingStatusHistoryRepository.findByBookingIdOrderByCreatedAtAscIdAsc(bookingId).stream()
                .map(this::mapTimeline)
                .toList();

        BigDecimal estimatedRefund = estimateRefundAmount(booking);
        return BookingDetailResponse.builder()
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .status(booking.getStatus())
                .statusLabel(mapStatusLabel(booking.getStatus()))
                .ownerUserId(ownerUserId)
                .providerId(booking.getProvider() != null ? booking.getProvider().getId() : null)
                .providerName(booking.getProviderNameSnapshot())
                .providerPhone(booking.getProviderPhoneSnapshot())
                .providerAddress(booking.getProviderAddressSnapshot())
                .providerImage(resolveProviderImage(booking))
                .providerServiceId(booking.getProviderService() != null ? booking.getProviderService().getId() : null)
                .serviceName(booking.getServiceNameSnapshot())
                .serviceDescription(booking.getServiceDescriptionSnapshot())
                .serviceDurationMinutes(booking.getServiceDurationMinutesSnapshot())
                .petId(booking.getPet() != null ? booking.getPet().getId() : null)
                .petName(booking.getPetNameSnapshot())
                .petBreed(booking.getPetBreedSnapshot())
                .petAvatarUrl(booking.getPet() != null ? booking.getPet().getAvatarUrl() : null)
                .appointmentDate(formatIsoDate(booking.getAppointmentDate()))
                .appointmentDateDisplay(formatDate(booking.getAppointmentDate()))
                .appointmentTime(formatTimeRange(booking.getStartTime(), booking.getEndTime()))
                .timezone(booking.getTimezone())
                .customerNote(booking.getCustomerNote())
                .subtotalAmount(defaultMoney(booking.getSubtotalAmount()))
                .promoDiscountAmount(defaultMoney(booking.getPromoDiscountAmount()))
                .taxAmount(defaultMoney(booking.getTaxAmount()))
                .totalAmount(defaultMoney(booking.getTotalAmount()))
                .totalAmountDisplay(formatMoney(booking.getTotalAmount()))
                .currencyCode(firstNonBlank(booking.getCurrencyCode(), "VND"))
                .invoiceId(invoice != null ? invoice.getId() : null)
                .invoiceNumber(invoice != null ? invoice.getInvoiceNumber() : null)
                .invoiceStatus(invoice != null ? invoice.getStatus() : null)
                .paymentStatus(payment != null ? payment.getStatus() : null)
                .paymentMethod(payment != null ? payment.getPaymentMethod() : null)
                .rescheduleCount(Optional.ofNullable(booking.getRescheduleCount()).orElse(0))
                .cancellationFreeHours(booking.getProvider() != null ? Optional.ofNullable(booking.getProvider().getCancellationFreeHours()).orElse(24) : 24)
                .cancellationDeadline(buildCancellationDeadline(booking))
                .estimatedRefundAmount(estimatedRefund)
                .estimatedRefundDisplay(formatMoney(estimatedRefund))
                .canCancel(canCancel(booking))
                .canReschedule(canReschedule(booking))
                .canReview(canReview(booking))
                .timeline(timeline)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingRescheduleContextResponse getRescheduleContext(Long ownerUserId, Long bookingId) {
        Booking booking = getOwnedBooking(ownerUserId, bookingId);
        if (!canReschedule(booking)) {
            throw new BadRequestException("Booking hiện không thể đổi lịch");
        }

        LocalDate today = LocalDate.now(APP_ZONE);
        List<ProviderAvailabilitySlot> slots = providerAvailabilitySlotRepository.findUpcomingAvailableSlotsForProvider(
                booking.getProvider().getId(),
                today,
                today.plusDays(14)
        ).stream()
                .filter(slot -> slot.getProviderService() != null && booking.getProviderService() != null && Objects.equals(slot.getProviderService().getId(), booking.getProviderService().getId()))
                .filter(slot -> !Objects.equals(slot.getId(), booking.getAvailabilitySlot() != null ? booking.getAvailabilitySlot().getId() : null))
                .toList();

        List<String> availableDates = slots.stream()
                .map(ProviderAvailabilitySlot::getSlotDate)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .map(ISO_DATE::format)
                .toList();

        List<BookingSlotOptionResponse> slotResponses = slots.stream().map(this::mapSlot).toList();

        return BookingRescheduleContextResponse.builder()
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .currentStatus(booking.getStatus())
                .currentStatusLabel(mapStatusLabel(booking.getStatus()))
                .providerId(booking.getProvider() != null ? booking.getProvider().getId() : null)
                .providerName(booking.getProviderNameSnapshot())
                .providerServiceId(booking.getProviderService() != null ? booking.getProviderService().getId() : null)
                .serviceName(booking.getServiceNameSnapshot())
                .currentDate(formatIsoDate(booking.getAppointmentDate()))
                .currentDateDisplay(formatDate(booking.getAppointmentDate()))
                .currentTime(booking.getStartTime() != null ? booking.getStartTime().format(TIME_VIEW) : null)
                .currentTimeDisplay(formatTimeRange(booking.getStartTime(), booking.getEndTime()))
                .rescheduleCount(Optional.ofNullable(booking.getRescheduleCount()).orElse(0))
                .availableDates(availableDates)
                .slots(slotResponses)
                .canReschedule(true)
                .note("Chọn slot mới cùng dịch vụ với booking hiện tại.")
                .build();
    }

    @Override
    @Transactional
    public BookingMutationResponse rescheduleBooking(Long ownerUserId, Long bookingId, BookingRescheduleRequest request) {
        if (!Objects.equals(ownerUserId, request.ownerUserId())) {
            throw new BadRequestException("ownerUserId không khớp với đường dẫn");
        }
        Booking booking = getOwnedBooking(ownerUserId, bookingId);
        if (!canReschedule(booking)) {
            throw new BadRequestException("Booking hiện không thể đổi lịch");
        }

        ProviderAvailabilitySlot newSlot = resolveNewSlot(booking, request);
        LocalDate oldDate = booking.getAppointmentDate();
        LocalTime oldStart = booking.getStartTime();
        LocalTime oldEnd = booking.getEndTime();
        ProviderAvailabilitySlot oldSlot = booking.getAvailabilitySlot();

        if (oldSlot != null) {
            releaseSlot(oldSlot);
        }
        occupySlot(newSlot);

        booking.setAvailabilitySlot(newSlot);
        booking.setAppointmentDate(newSlot.getSlotDate());
        booking.setStartTime(newSlot.getStartTime());
        booking.setEndTime(newSlot.getEndTime());
        booking.setRescheduleCount(Optional.ofNullable(booking.getRescheduleCount()).orElse(0) + 1);
        bookingRepository.save(booking);

        BookingReschedule reschedule = new BookingReschedule();
        reschedule.setBooking(booking);
        reschedule.setRequestedByUser(booking.getCustomerUser());
        reschedule.setOldAppointmentDate(oldDate);
        reschedule.setOldStartTime(oldStart);
        reschedule.setOldEndTime(oldEnd);
        reschedule.setNewAppointmentDate(newSlot.getSlotDate());
        reschedule.setNewStartTime(newSlot.getStartTime());
        reschedule.setNewEndTime(newSlot.getEndTime());
        reschedule.setFeeAmount(BigDecimal.ZERO);
        reschedule.setStatus("APPLIED");
        reschedule.setNote(normalizeBlank(request.note()));
        bookingRescheduleRepository.save(reschedule);

        BookingStatusHistory history = new BookingStatusHistory();
        history.setBooking(booking);
        history.setFromStatus(booking.getStatus());
        history.setToStatus(booking.getStatus());
        history.setChangedByUser(booking.getCustomerUser());
        history.setNote("Đổi lịch từ " + formatDate(oldDate) + " " + formatTimeRange(oldStart, oldEnd)
                + " sang " + formatDate(newSlot.getSlotDate()) + " " + formatTimeRange(newSlot.getStartTime(), newSlot.getEndTime()));
        bookingStatusHistoryRepository.save(history);

        return BookingMutationResponse.builder()
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .status(booking.getStatus())
                .statusLabel(mapStatusLabel(booking.getStatus()))
                .appointmentDate(formatIsoDate(booking.getAppointmentDate()))
                .appointmentDateDisplay(formatDate(booking.getAppointmentDate()))
                .appointmentTime(formatTimeRange(booking.getStartTime(), booking.getEndTime()))
                .slotId(newSlot.getId())
                .message("Đổi lịch thành công")
                .build();
    }

    @Override
    @Transactional
    public BookingMutationResponse cancelBooking(Long ownerUserId, Long bookingId, BookingCancelRequest request) {
        if (!Objects.equals(ownerUserId, request.ownerUserId())) {
            throw new BadRequestException("ownerUserId không khớp với đường dẫn");
        }
        Booking booking = getOwnedBooking(ownerUserId, bookingId);
        if (!canCancel(booking)) {
            throw new BadRequestException("Booking hiện không thể hủy");
        }

        if (bookingCancellationRepository.findByBookingId(bookingId).isPresent()) {
            throw new BadRequestException("Booking này đã được hủy trước đó");
        }

        BigDecimal refundAmount = estimateRefundAmount(booking);
        String refundStatus = refundAmount.compareTo(BigDecimal.ZERO) > 0 ? "FULL" : "NOT_REQUIRED";
        String previousStatus = booking.getStatus();

        if (booking.getAvailabilitySlot() != null) {
            releaseSlot(booking.getAvailabilitySlot());
        }

        booking.setStatus("CANCELLED");
        booking.setCancellationReasonCode(request.reasonCode());
        bookingRepository.save(booking);

        BookingCancellation cancellation = new BookingCancellation();
        cancellation.setBooking(booking);
        cancellation.setCancelledByUser(booking.getCustomerUser());
        cancellation.setReasonCode(request.reasonCode());
        cancellation.setReasonText(normalizeBlank(request.reasonText()));
        cancellation.setRefundStatus(refundStatus);
        cancellation.setRefundAmount(refundAmount);
        bookingCancellationRepository.save(cancellation);

        BookingStatusHistory history = new BookingStatusHistory();
        history.setBooking(booking);
        history.setFromStatus(previousStatus);
        history.setToStatus("CANCELLED");
        history.setChangedByUser(booking.getCustomerUser());
        history.setNote("Hủy booking. Lý do: " + request.reasonCode() + (normalizeBlank(request.reasonText()) != null ? " - " + normalizeBlank(request.reasonText()) : ""));
        bookingStatusHistoryRepository.save(history);

        return BookingMutationResponse.builder()
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .status(booking.getStatus())
                .statusLabel(mapStatusLabel(booking.getStatus()))
                .appointmentDate(formatIsoDate(booking.getAppointmentDate()))
                .appointmentDateDisplay(formatDate(booking.getAppointmentDate()))
                .appointmentTime(formatTimeRange(booking.getStartTime(), booking.getEndTime()))
                .slotId(booking.getAvailabilitySlot() != null ? booking.getAvailabilitySlot().getId() : null)
                .message("Đã hủy booking thành công")
                .refundAmount(refundAmount)
                .refundAmountDisplay(formatMoney(refundAmount))
                .refundStatus(refundStatus)
                .build();
    }

    private Booking getOwnedBooking(Long ownerUserId, Long bookingId) {
        return bookingRepository.findDetailedOwnedById(ownerUserId, bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking của người dùng này"));
    }

    private String normalizeStatusFilter(String status) {
        if (status == null || status.isBlank()) return "ALL";
        return status.trim().toUpperCase(Locale.ROOT);
    }

    private boolean matchesFilter(String bookingStatus, String filter) {
        String normalized = bookingStatus != null ? bookingStatus.toUpperCase(Locale.ROOT) : "";
        return switch (filter) {
            case "PENDING" -> isUiPending(normalized);
            case "CONFIRMED" -> "CONFIRMED".equals(normalized);
            case "COMPLETED" -> "COMPLETED".equals(normalized);
            case "CANCELLED" -> "CANCELLED".equals(normalized);
            default -> true;
        };
    }

    private boolean isUiPending(String status) {
        String normalized = status != null ? status.toUpperCase(Locale.ROOT) : "";
        return List.of("PENDING_PAYMENT", "PENDING_CONFIRMATION").contains(normalized);
    }

    private BookingListItemResponse mapListItem(Booking booking) {
        Invoice invoice = invoiceRepository.findByBookingId(booking.getId()).orElse(null);
        return BookingListItemResponse.builder()
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .status(booking.getStatus())
                .statusLabel(mapStatusLabel(booking.getStatus()))
                .providerName(booking.getProviderNameSnapshot())
                .providerImage(resolveProviderImage(booking))
                .serviceName(booking.getServiceNameSnapshot())
                .petLabel(buildPetLabel(booking))
                .appointmentDate(formatIsoDate(booking.getAppointmentDate()))
                .appointmentDateDisplay(formatDate(booking.getAppointmentDate()))
                .appointmentTime(formatTimeRange(booking.getStartTime(), booking.getEndTime()))
                .totalAmount(defaultMoney(booking.getTotalAmount()))
                .totalAmountDisplay(formatMoney(booking.getTotalAmount()))
                .currencyCode(firstNonBlank(booking.getCurrencyCode(), "VND"))
                .invoiceId(invoice != null ? invoice.getId() : null)
                .canCancel(canCancel(booking))
                .canReschedule(canReschedule(booking))
                .canReview(canReview(booking))
                .build();
    }

    private BookingTimelineItemResponse mapTimeline(BookingStatusHistory item) {
        String changedBy = item.getChangedByUser() != null ? firstNonBlank(item.getChangedByUser().getFullName(), item.getChangedByUser().getEmail()) : "PetGo";
        return BookingTimelineItemResponse.builder()
                .fromStatus(item.getFromStatus())
                .fromStatusLabel(mapStatusLabel(item.getFromStatus()))
                .toStatus(item.getToStatus())
                .toStatusLabel(mapStatusLabel(item.getToStatus()))
                .note(item.getNote())
                .changedBy(changedBy)
                .createdAt(item.getCreatedAt() != null ? item.getCreatedAt().atZone(APP_ZONE).format(DATE_TIME_VIEW) : null)
                .build();
    }

    private ProviderAvailabilitySlot resolveNewSlot(Booking booking, BookingRescheduleRequest request) {
        ProviderAvailabilitySlot newSlot = null;
        if (request.newSlotId() != null) {
            newSlot = providerAvailabilitySlotRepository.findDetailedById(request.newSlotId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy slot mới"));
        } else if (request.newAppointmentDate() != null && request.newStartTime() != null) {
            LocalDate date = parseDate(request.newAppointmentDate());
            LocalTime time = parseTime(request.newStartTime());
            List<ProviderAvailabilitySlot> slots = providerAvailabilitySlotRepository.findUpcomingAvailableSlotsForProvider(
                    booking.getProvider().getId(), LocalDate.now(APP_ZONE), LocalDate.now(APP_ZONE).plusDays(30));
            newSlot = slots.stream()
                    .filter(slot -> slot.getProviderService() != null && booking.getProviderService() != null && Objects.equals(slot.getProviderService().getId(), booking.getProviderService().getId()))
                    .filter(slot -> Objects.equals(slot.getSlotDate(), date))
                    .filter(slot -> Objects.equals(slot.getStartTime(), time))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Không tìm thấy slot mới phù hợp"));
        }

        if (newSlot == null) {
            throw new BadRequestException("Vui lòng chọn slot mới");
        }
        if (booking.getProviderService() == null || newSlot.getProviderService() == null || !Objects.equals(newSlot.getProviderService().getId(), booking.getProviderService().getId())) {
            throw new BadRequestException("Slot mới không thuộc dịch vụ hiện tại");
        }
        if (!Objects.equals(newSlot.getProvider().getId(), booking.getProvider().getId())) {
            throw new BadRequestException("Slot mới không thuộc nhà cung cấp hiện tại");
        }
        if (!"AVAILABLE".equalsIgnoreCase(firstNonBlank(newSlot.getSlotStatus(), "AVAILABLE")) || Optional.ofNullable(newSlot.getCapacityBooked()).orElse(0) >= Optional.ofNullable(newSlot.getCapacityTotal()).orElse(1)) {
            throw new BadRequestException("Slot mới không còn khả dụng");
        }
        if (Objects.equals(booking.getAvailabilitySlot() != null ? booking.getAvailabilitySlot().getId() : null, newSlot.getId())) {
            throw new BadRequestException("Slot mới trùng với lịch hiện tại");
        }
        return newSlot;
    }

    private void occupySlot(ProviderAvailabilitySlot slot) {
        slot.setCapacityBooked(Optional.ofNullable(slot.getCapacityBooked()).orElse(0) + 1);
        if (slot.getCapacityBooked() >= Optional.ofNullable(slot.getCapacityTotal()).orElse(1)) {
            slot.setSlotStatus("BOOKED");
        }
        providerAvailabilitySlotRepository.save(slot);
    }

    private void releaseSlot(ProviderAvailabilitySlot slot) {
        int booked = Math.max(0, Optional.ofNullable(slot.getCapacityBooked()).orElse(0) - 1);
        slot.setCapacityBooked(booked);
        if (!"UNAVAILABLE".equalsIgnoreCase(firstNonBlank(slot.getSlotStatus(), "AVAILABLE"))) {
            slot.setSlotStatus("AVAILABLE");
        }
        providerAvailabilitySlotRepository.save(slot);
    }

    private BookingSlotOptionResponse mapSlot(ProviderAvailabilitySlot slot) {
        int capacityTotal = Optional.ofNullable(slot.getCapacityTotal()).orElse(1);
        int capacityBooked = Optional.ofNullable(slot.getCapacityBooked()).orElse(0);
        int remaining = Math.max(capacityTotal - capacityBooked, 0);
        return BookingSlotOptionResponse.builder()
                .slotId(slot.getId())
                .providerServiceId(slot.getProviderService() != null ? slot.getProviderService().getId() : null)
                .serviceName(slot.getProviderService() != null && slot.getProviderService().getService() != null ? slot.getProviderService().getService().getName() : null)
                .date(formatIsoDate(slot.getSlotDate()))
                .startTime(slot.getStartTime() != null ? slot.getStartTime().format(TIME_VIEW) : null)
                .endTime(slot.getEndTime() != null ? slot.getEndTime().format(TIME_VIEW) : null)
                .label(formatTimeRange(slot.getStartTime(), slot.getEndTime()))
                .capacityRemaining(remaining)
                .selected(false)
                .build();
    }

    private boolean canCancel(Booking booking) {
        if (booking == null || booking.getAppointmentDate() == null || booking.getStartTime() == null) return false;
        String status = firstNonBlank(booking.getStatus(), "").toUpperCase(Locale.ROOT);
        if (List.of("CANCELLED", "COMPLETED", "NO_SHOW").contains(status)) return false;
        return LocalDateTime.of(booking.getAppointmentDate(), booking.getStartTime()).isAfter(LocalDateTime.now(APP_ZONE));
    }

    private boolean canReschedule(Booking booking) {
        if (booking == null || booking.getAppointmentDate() == null || booking.getStartTime() == null) return false;
        String status = firstNonBlank(booking.getStatus(), "").toUpperCase(Locale.ROOT);
        if (!List.of("PENDING_CONFIRMATION", "CONFIRMED").contains(status)) return false;
        return LocalDateTime.of(booking.getAppointmentDate(), booking.getStartTime()).isAfter(LocalDateTime.now(APP_ZONE));
    }

    private boolean canReview(Booking booking) {
        return booking != null
                && "COMPLETED".equalsIgnoreCase(firstNonBlank(booking.getStatus(), ""))
                && !reviewRepository.existsByBooking_IdAndDeletedAtIsNull(booking.getId());
    }

    private BigDecimal estimateRefundAmount(Booking booking) {
        if (booking == null) return BigDecimal.ZERO;
        LocalDateTime appointmentAt = booking.getAppointmentDate() != null && booking.getStartTime() != null
                ? LocalDateTime.of(booking.getAppointmentDate(), booking.getStartTime())
                : null;
        if (appointmentAt == null) return BigDecimal.ZERO;
        int freeHours = booking.getProvider() != null ? Optional.ofNullable(booking.getProvider().getCancellationFreeHours()).orElse(24) : 24;
        LocalDateTime deadline = appointmentAt.minusHours(freeHours);
        if (LocalDateTime.now(APP_ZONE).isAfter(deadline)) return BigDecimal.ZERO;
        return defaultMoney(booking.getTotalAmount());
    }

    private String buildCancellationDeadline(Booking booking) {
        if (booking == null || booking.getAppointmentDate() == null || booking.getStartTime() == null) return null;
        int freeHours = booking.getProvider() != null ? Optional.ofNullable(booking.getProvider().getCancellationFreeHours()).orElse(24) : 24;
        LocalDateTime deadline = LocalDateTime.of(booking.getAppointmentDate(), booking.getStartTime()).minusHours(freeHours);
        return deadline.format(DATE_TIME_VIEW);
    }

    private String mapStatusLabel(String status) {
        if (status == null) return "Chưa rõ";
        return switch (status.toUpperCase(Locale.ROOT)) {
            case "PENDING_PAYMENT" -> "Chờ thanh toán";
            case "PENDING_CONFIRMATION" -> "Chờ xác nhận";
            case "CONFIRMED" -> "Đã xác nhận";
            case "IN_PROGRESS" -> "Đang diễn ra";
            case "COMPLETED" -> "Hoàn thành";
            case "CANCELLED" -> "Đã hủy";
            case "NO_SHOW" -> "Không đến";
            default -> status;
        };
    }

    private String resolveProviderImage(Booking booking) {
        if (booking.getProvider() == null) return null;
        return firstNonBlank(booking.getProvider().getMainImageUrl(), booking.getProvider().getCoverImageUrl());
    }

    private String buildPetLabel(Booking booking) {
        if (booking == null) return "";
        if (booking.getPetBreedSnapshot() != null && !booking.getPetBreedSnapshot().isBlank()) {
            return booking.getPetNameSnapshot() + " (" + booking.getPetBreedSnapshot() + ")";
        }
        return firstNonBlank(booking.getPetNameSnapshot(), "Thú cưng");
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0 đ";
        return String.format(Locale.forLanguageTag("vi-VN"), "%,.0f đ", amount);
    }

    private String formatIsoDate(LocalDate date) {
        return date != null ? date.format(ISO_DATE) : null;
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_VIEW) : null;
    }

    private String formatTimeRange(LocalTime start, LocalTime end) {
        if (start == null) return null;
        String startStr = start.format(TIME_VIEW);
        return end == null ? startStr : startStr + " - " + end.format(TIME_VIEW);
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, ISO_DATE);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Ngày mới không hợp lệ, cần định dạng yyyy-MM-dd");
        }
    }

    private LocalTime parseTime(String value) {
        try {
            return LocalTime.parse(value, TIME_VIEW);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Giờ mới không hợp lệ, cần định dạng HH:mm");
        }
    }

    private String normalizeBlank(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String value : values) {
            if (value != null && !value.isBlank()) return value.trim();
        }
        return null;
    }
}
