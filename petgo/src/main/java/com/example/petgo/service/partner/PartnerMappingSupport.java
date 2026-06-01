package com.example.petgo.service.partner;

import com.example.petgo.dto.BookingTimelineItemResponse;
import com.example.petgo.dto.partner.*;
import com.example.petgo.entity.*;
import com.example.petgo.repository.BookingRepository;
import com.example.petgo.repository.InvoiceRepository;
import com.example.petgo.repository.PaymentRepository;
import com.example.petgo.repository.ServiceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RequiredArgsConstructor
public class PartnerMappingSupport {

    public static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    public static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter DATE_VIEW = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DATE_TIME_VIEW = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter TIME_VIEW = DateTimeFormatter.ofPattern("HH:mm");

    private final BookingRepository bookingRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    public PartnerProfileResponse mapProfile(ProviderProfile provider,
            RegistrationApplication registration,
            List<ProviderPhoto> photos,
            List<ServiceCategory> registeredCategories) {
        return PartnerProfileResponse.builder()
                .providerId(provider.getId())
                .userId(provider.getUser() != null ? provider.getUser().getId() : null)
                .providerCode(provider.getProviderCode())
                .businessName(provider.getBusinessName())
                .slug(provider.getSlug())
                .providerType(provider.getProviderType())
                .headline(provider.getHeadline())
                .description(provider.getDescription())
                .yearsExperience(provider.getYearsExperience())
                .verificationStatus(provider.getVerificationStatus())
                .acceptsInstantBooking(Boolean.TRUE.equals(provider.getAcceptsInstantBooking()))
                .acceptsMembership(Boolean.TRUE.equals(provider.getAcceptsMembership()))
                .averageRating(defaultMoney(provider.getAverageRating()))
                .totalReviews(Optional.ofNullable(provider.getTotalReviews()).orElse(0))
                .totalCompletedBookings(Optional.ofNullable(provider.getTotalCompletedBookings()).orElse(0))
                .cancellationFreeHours(Optional.ofNullable(provider.getCancellationFreeHours()).orElse(24))
                .emergencyPhone(provider.getEmergencyPhone())
                .primaryAddressLine1(provider.getPrimaryAddressLine1())
                .primaryAddressLine2(provider.getPrimaryAddressLine2())
                .ward(provider.getWard())
                .district(provider.getDistrict())
                .city(provider.getCity())
                .province(provider.getProvince())
                .countryCode(provider.getCountryCode())
                .address(buildAddress(provider))
                .latitude(provider.getLatitude())
                .longitude(provider.getLongitude())
                .mainImageUrl(provider.getMainImageUrl())
                .coverImageUrl(provider.getCoverImageUrl())
                .priceFromAmount(provider.getPriceFromAmount())
                .currencyCode(firstNonBlank(provider.getCurrencyCode(), "VND"))
                .status(provider.getStatus())
                .photoUrls((photos == null ? List.<ProviderPhoto>of() : photos).stream()
                        .map(ProviderPhoto::getPhotoUrl)
                        .filter(Objects::nonNull)
                        .toList())
                .registeredCategories(
                        (registeredCategories == null ? List.<ServiceCategory>of() : registeredCategories).stream()
                                .map(this::mapCategory)
                                .toList())
                .approvedAt(registration != null && registration.getReviewedAt() != null
                        ? registration.getReviewedAt().format(DATE_TIME_VIEW)
                        : null)
                .build();
    }

    public PartnerServiceResponse mapService(ProviderService providerService) {
        CatalogService service = providerService.getService();
        ServiceCategory category = service != null ? service.getCategory() : null;
        ServiceCategory parentCategory = category != null ? category.getParent() : null;
        String displayName = firstNonBlank(providerService.getCustomName(), service != null ? service.getName() : null);
        List<Long> categoryIds = parseLongCsv(providerService.getCategoryIds());
        if (categoryIds.isEmpty() && category != null && category.getId() != null) {
            categoryIds = List.of(category.getId());
        }

        return PartnerServiceResponse.builder()
                .id(providerService.getId())
                .providerId(providerService.getProvider() != null ? providerService.getProvider().getId() : null)
                .serviceId(service != null ? service.getId() : null)
                .serviceName(service != null ? service.getName() : null)
                .customName(providerService.getCustomName())
                .displayName(displayName)
                .shortDescription(firstNonBlank(providerService.getShortDescription(),
                        service != null ? service.getShortDescription() : null))
                .description(firstNonBlank(providerService.getDescription(),
                        service != null ? service.getDescription() : null))
                .durationMinutes(providerService.getDurationMinutes())
                .priceAmount(defaultMoney(providerService.getPriceAmount()))
                .priceDisplay(formatMoney(providerService.getPriceAmount()))
                .currencyCode(firstNonBlank(providerService.getCurrencyCode(),
                        service != null ? service.getCurrencyCode() : null, "VND"))
                .priceUnit(firstNonBlank(providerService.getPriceUnit(),
                        service != null ? service.getPriceUnit() : null, "SESSION"))
                .featured(Boolean.TRUE.equals(providerService.getFeatured()))
                .active(Boolean.TRUE.equals(providerService.getActive()))
                .capacityPerSlot(Optional.ofNullable(providerService.getCapacityPerSlot()).orElse(1))
                .bookingBufferMinutes(Optional.ofNullable(providerService.getBookingBufferMinutes()).orElse(0))
                .displayOrder(Optional.ofNullable(providerService.getDisplayOrder()).orElse(0))
                .categoryId(category != null ? category.getId() : null)
                .categoryName(category != null ? category.getName() : null)
                .parentCategoryId(parentCategory != null ? parentCategory.getId() : null)
                .parentCategoryName(parentCategory != null ? parentCategory.getName() : null)
                .categoryIds(categoryIds)
                .categories(mapServiceCategories(categoryIds, providerService))
                .photoUrls(parseTextLines(providerService.getPhotoUrls()))
                .approvalStatus(firstNonBlank(providerService.getApprovalStatus(), "APPROVED"))
                .bookingCount(providerService.getId() != null
                        ? bookingRepository.countByProviderService_Id(providerService.getId())
                        : 0)
                .build();
    }

    public PartnerBusinessHourResponse mapHour(ProviderBusinessHour hour) {
        return PartnerBusinessHourResponse.builder()
                .id(hour.getId())
                .weekday(hour.getWeekday())
                .weekdayLabel(mapWeekdayLabel(hour.getWeekday()))
                .opensAt(formatTime(hour.getOpensAt()))
                .closesAt(formatTime(hour.getClosesAt()))
                .breakStartsAt(formatTime(hour.getBreakStartsAt()))
                .breakEndsAt(formatTime(hour.getBreakEndsAt()))
                .closed(Boolean.TRUE.equals(hour.getClosed()))
                .timeLabel(buildHourLabel(hour))
                .build();
    }

    public PartnerSlotResponse mapSlot(ProviderAvailabilitySlot slot) {
        int capacityTotal = Optional.ofNullable(slot.getCapacityTotal()).orElse(1);
        int capacityBooked = Optional.ofNullable(slot.getCapacityBooked()).orElse(0);
        return PartnerSlotResponse.builder()
                .id(slot.getId())
                .providerServiceId(slot.getProviderService() != null ? slot.getProviderService().getId() : null)
                .serviceName(resolveServiceName(slot.getProviderService()))
                .date(formatIsoDate(slot.getSlotDate()))
                .dateDisplay(formatDate(slot.getSlotDate()))
                .startTime(formatTime(slot.getStartTime()))
                .endTime(formatTime(slot.getEndTime()))
                .timeLabel(formatTimeRange(slot.getStartTime(), slot.getEndTime()))
                .slotStatus(slot.getSlotStatus())
                .capacityTotal(capacityTotal)
                .capacityBooked(capacityBooked)
                .capacityRemaining(Math.max(0, capacityTotal - capacityBooked))
                .note(slot.getNote())
                .build();
    }

    public PartnerBookingSummaryResponse mapBookingSummary(Booking booking) {
        Invoice invoice = invoiceRepository.findByBookingId(booking.getId()).orElse(null);
        Payment payment = invoice != null
                ? paymentRepository.findTopByInvoiceIdOrderByCreatedAtDesc(invoice.getId()).orElse(null)
                : null;
        return PartnerBookingSummaryResponse.builder()
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .status(booking.getStatus())
                .statusLabel(mapStatusLabel(booking.getStatus()))
                .customerUserId(booking.getCustomerUser() != null ? booking.getCustomerUser().getId() : null)
                .customerName(booking.getCustomerUser() != null ? booking.getCustomerUser().getFullName() : null)
                .customerPhone(booking.getCustomerUser() != null ? booking.getCustomerUser().getPhoneNumber() : null)
                .serviceName(booking.getServiceNameSnapshot())
                .providerServiceId(booking.getProviderService() != null ? booking.getProviderService().getId() : null)
                .petId(booking.getPet() != null ? booking.getPet().getId() : null)
                .petName(booking.getPetNameSnapshot())
                .petBreed(booking.getPetBreedSnapshot())
                .appointmentDate(formatIsoDate(booking.getAppointmentDate()))
                .appointmentDateDisplay(formatDate(booking.getAppointmentDate()))
                .appointmentTime(formatTimeRange(booking.getStartTime(), booking.getEndTime()))
                .totalAmount(defaultMoney(booking.getTotalAmount()))
                .totalAmountDisplay(formatMoney(booking.getTotalAmount()))
                .currencyCode(firstNonBlank(booking.getCurrencyCode(), "VND"))
                .paymentStatus(payment != null ? payment.getStatus() : null)
                .customerNote(booking.getCustomerNote())
                .canConfirm(canConfirm(booking))
                .canStart(canStart(booking))
                .canComplete(canComplete(booking))
                .canCancel(canPartnerCancel(booking))
                .build();
    }

    public PartnerBookingDetailResponse mapBookingDetail(Booking booking, List<BookingStatusHistory> histories) {
        Invoice invoice = invoiceRepository.findByBookingId(booking.getId()).orElse(null);
        Payment payment = invoice != null
                ? paymentRepository.findTopByInvoiceIdOrderByCreatedAtDesc(invoice.getId()).orElse(null)
                : null;
        List<BookingTimelineItemResponse> timeline = (histories == null ? List.<BookingStatusHistory>of() : histories)
                .stream()
                .map(this::mapTimeline)
                .toList();

        return PartnerBookingDetailResponse.builder()
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .status(booking.getStatus())
                .statusLabel(mapStatusLabel(booking.getStatus()))
                .providerId(booking.getProvider() != null ? booking.getProvider().getId() : null)
                .providerName(booking.getProviderNameSnapshot())
                .customerUserId(booking.getCustomerUser() != null ? booking.getCustomerUser().getId() : null)
                .customerName(booking.getCustomerUser() != null ? booking.getCustomerUser().getFullName() : null)
                .customerPhone(booking.getCustomerUser() != null ? booking.getCustomerUser().getPhoneNumber() : null)
                .customerEmail(booking.getCustomerUser() != null ? booking.getCustomerUser().getEmail() : null)
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
                .internalNote(booking.getInternalNote())
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
                .canConfirm(canConfirm(booking))
                .canStart(canStart(booking))
                .canComplete(canComplete(booking))
                .canCancel(canPartnerCancel(booking))
                .timeline(timeline)
                .build();
    }

    public BookingTimelineItemResponse mapTimeline(BookingStatusHistory item) {
        String changedBy = item.getChangedByUser() != null
                ? firstNonBlank(item.getChangedByUser().getFullName(), item.getChangedByUser().getEmail())
                : "PetGo";
        return BookingTimelineItemResponse.builder()
                .fromStatus(item.getFromStatus())
                .fromStatusLabel(mapStatusLabel(item.getFromStatus()))
                .toStatus(item.getToStatus())
                .toStatusLabel(mapStatusLabel(item.getToStatus()))
                .note(item.getNote())
                .changedBy(changedBy)
                .createdAt(item.getCreatedAt() != null ? item.getCreatedAt().atZone(APP_ZONE).format(DATE_TIME_VIEW)
                        : null)
                .build();
    }

    public boolean canConfirm(Booking booking) {
        return booking != null && "PENDING_CONFIRMATION".equalsIgnoreCase(firstNonBlank(booking.getStatus(), ""));
    }

    public boolean canStart(Booking booking) {
        return booking != null && "CONFIRMED".equalsIgnoreCase(firstNonBlank(booking.getStatus(), ""));
    }

    public boolean canComplete(Booking booking) {
        return booking != null && "IN_PROGRESS".equalsIgnoreCase(firstNonBlank(booking.getStatus(), ""));
    }

    public boolean canPartnerCancel(Booking booking) {
        if (booking == null)
            return false;
        String status = firstNonBlank(booking.getStatus(), "").toUpperCase(Locale.ROOT);
        return !List.of("COMPLETED", "CANCELLED", "NO_SHOW", "REFUNDED").contains(status);
    }

    public boolean isPending(String status) {
        String normalized = firstNonBlank(status, "").toUpperCase(Locale.ROOT);
        return List.of("PENDING_PAYMENT", "PENDING_CONFIRMATION").contains(normalized);
    }

    public boolean isUpcoming(Booking booking) {
        return booking != null
                && booking.getAppointmentDate() != null
                && !List.of("COMPLETED", "CANCELLED", "NO_SHOW", "REFUNDED")
                        .contains(firstNonBlank(booking.getStatus(), "").toUpperCase(Locale.ROOT))
                && !booking.getAppointmentDate().isBefore(LocalDate.now(APP_ZONE));
    }

    public BigDecimal defaultMoney(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    public String formatMoney(BigDecimal amount) {
        if (amount == null)
            return "0 đ";
        return String.format(Locale.forLanguageTag("vi-VN"), "%,.0f đ", amount);
    }

    public String mapStatusLabel(String status) {
        if (status == null)
            return "Chưa rõ";
        return switch (status.toUpperCase(Locale.ROOT)) {
            case "PENDING_PAYMENT" -> "Chờ thanh toán";
            case "PENDING_CONFIRMATION" -> "Chờ xác nhận";
            case "CONFIRMED" -> "Đã xác nhận";
            case "IN_PROGRESS" -> "Đang phục vụ";
            case "COMPLETED" -> "Hoàn thành";
            case "CANCELLED" -> "Đã hủy";
            case "NO_SHOW" -> "Không đến";
            case "REFUNDED" -> "Đã hoàn tiền";
            default -> status;
        };
    }

    public String firstNonBlank(String... values) {
        if (values == null)
            return null;
        for (String value : values) {
            if (value != null && !value.isBlank())
                return value.trim();
        }
        return null;
    }

    public String normalizeBlank(String value) {
        if (value == null)
            return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public List<String> parseTextLines(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return Arrays.stream(text.split("\\R"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();
    }

    public List<Long> parseLongCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(value -> {
                    try {
                        return Long.parseLong(value);
                    } catch (NumberFormatException ignored) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    public String formatIsoDate(LocalDate date) {
        return date != null ? date.format(ISO_DATE) : null;
    }

    public String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_VIEW) : null;
    }

    public String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_VIEW) : null;
    }

    public String formatTimeRange(LocalTime start, LocalTime end) {
        if (start == null)
            return null;
        String startStr = start.format(TIME_VIEW);
        return end == null ? startStr : startStr + " - " + end.format(TIME_VIEW);
    }

    public String mapWeekdayLabel(Integer weekday) {
        if (weekday == null)
            return "N/A";
        return switch (weekday) {
            case 1 -> "Thứ 2";
            case 2 -> "Thứ 3";
            case 3 -> "Thứ 4";
            case 4 -> "Thứ 5";
            case 5 -> "Thứ 6";
            case 6 -> "Thứ 7";
            case 7 -> "Chủ nhật";
            default -> "N/A";
        };
    }

    private PartnerServiceCategoryResponse mapCategory(ServiceCategory category) {
        return PartnerServiceCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .build();
    }

    private List<PartnerServiceCategoryResponse> mapServiceCategories(List<Long> categoryIds,
            ProviderService providerService) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }
        List<ServiceCategory> categories = serviceCategoryRepository.findAllById(categoryIds);
        if (!categories.isEmpty()) {
            return categoryIds.stream()
                    .map(id -> categories.stream().filter(category -> Objects.equals(category.getId(), id)).findFirst()
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .map(this::mapCategory)
                    .toList();
        }
        ServiceCategory fallback = providerService != null && providerService.getService() != null
                ? providerService.getService().getCategory()
                : null;
        return fallback == null ? List.of() : List.of(mapCategory(fallback));
    }

    private String buildAddress(ProviderProfile provider) {
        List<String> parts = java.util.stream.Stream.of(
                provider.getPrimaryAddressLine1(),
                provider.getPrimaryAddressLine2(),
                provider.getWard(),
                provider.getDistrict(),
                provider.getCity(),
                provider.getProvince())
                .filter(value -> value != null && !value.isBlank())
                .toList();
        return parts.isEmpty() ? null : String.join(", ", parts);
    }

    private String resolveServiceName(ProviderService providerService) {
        if (providerService == null)
            return null;
        return firstNonBlank(
                providerService.getCustomName(),
                providerService.getService() != null ? providerService.getService().getName() : null);
    }

    private String buildHourLabel(ProviderBusinessHour hour) {
        if (Boolean.TRUE.equals(hour.getClosed()))
            return "Đóng cửa";
        String range = formatTimeRange(hour.getOpensAt(), hour.getClosesAt());
        if (range == null)
            return "Chưa cấu hình";
        String breakRange = formatTimeRange(hour.getBreakStartsAt(), hour.getBreakEndsAt());
        return breakRange == null ? range : range + " (nghỉ " + breakRange + ")";
    }

    public String nowTimelineLabel() {
        return LocalDateTime.now(APP_ZONE).format(DATE_TIME_VIEW);
    }
}