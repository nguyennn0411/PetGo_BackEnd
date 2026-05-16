package com.example.petgo.service.impl;

import com.example.petgo.entity.Booking;
import com.example.petgo.entity.Invoice;
import com.example.petgo.entity.MembershipPlan;
import com.example.petgo.entity.MembershipSubscription;
import com.example.petgo.entity.PromoCode;
import com.example.petgo.entity.PromoCodeRedemption;
import com.example.petgo.entity.ProviderService;
import com.example.petgo.entity.ServiceCategory;
import com.example.petgo.entity.User;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.repository.BookingRepository;
import com.example.petgo.repository.MembershipSubscriptionRepository;
import com.example.petgo.repository.PromoCodeRedemptionRepository;
import com.example.petgo.repository.PromoCodeRepository;
import com.example.petgo.service.PromotionPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionPolicyServiceImpl implements PromotionPolicyService {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final List<String> ACTIVE_MEMBERSHIP_STATUSES = List.of("ACTIVE", "PAST_DUE", "PENDING_PAYMENT");

    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeRedemptionRepository promoCodeRedemptionRepository;
    private final BookingRepository bookingRepository;
    private final MembershipSubscriptionRepository membershipSubscriptionRepository;

    @Override
    @Transactional(readOnly = true)
    public PromoPreview previewForBooking(Booking booking, String rawPromoCode) {
        String promoCode = normalizeBlank(rawPromoCode);
        if (promoCode == null) {
            return new PromoPreview(null, null, BigDecimal.ZERO, null);
        }
        if (booking == null) {
            throw new BadRequestException("Không tìm thấy booking để áp dụng ưu đãi.");
        }

        PromoCode promo = requireActivePromo(promoCode);
        User user = booking.getCustomerUser();
        BigDecimal subtotal = defaultMoney(booking.getSubtotalAmount());

        validateCommonPolicy(promo, user, subtotal, "BOOKING");
        validateBookingScope(promo, booking, user);

        BigDecimal discount = calculatePromoDiscount(subtotal, promo);
        return new PromoPreview(promo, promo.getCode().toUpperCase(Locale.ROOT), discount,
                discount.compareTo(BigDecimal.ZERO) > 0
                        ? "Đã áp dụng mã " + promo.getCode().toUpperCase(Locale.ROOT)
                        : "Mã ưu đãi không tạo ra giảm giá");
    }

    @Override
    @Transactional(readOnly = true)
    public PromoPreview previewForMembership(User user, MembershipPlan plan, String rawPromoCode) {
        String promoCode = normalizeBlank(rawPromoCode);
        if (promoCode == null) {
            return new PromoPreview(null, null, BigDecimal.ZERO, null);
        }
        if (plan == null) {
            throw new BadRequestException("Không tìm thấy gói membership để áp dụng ưu đãi.");
        }

        PromoCode promo = requireActivePromo(promoCode);
        BigDecimal subtotal = defaultMoney(plan.getPriceAmount());

        validateCommonPolicy(promo, user, subtotal, "MEMBERSHIP");
        validateMembershipScope(promo, plan);

        BigDecimal discount = calculatePromoDiscount(subtotal, promo);
        return new PromoPreview(promo, promo.getCode().toUpperCase(Locale.ROOT), discount,
                discount.compareTo(BigDecimal.ZERO) > 0
                        ? "Đã áp dụng mã " + promo.getCode().toUpperCase(Locale.ROOT)
                        : "Mã ưu đãi không tạo ra giảm giá");
    }

    @Override
    @Transactional
    public void recordBookingRedemption(PromoPreview preview, User user, Booking booking, Invoice invoice) {
        if (preview == null || !preview.applied() || invoice == null || invoice.getId() == null || user == null) {
            return;
        }
        if (promoCodeRedemptionRepository.existsByInvoice_IdAndPromoCode_Id(invoice.getId(),
                preview.promoCode().getId())) {
            return;
        }
        PromoCode promo = preview.promoCode();
        PromoCodeRedemption redemption = new PromoCodeRedemption();
        redemption.setPromoCode(promo);
        redemption.setUser(user);
        redemption.setInvoice(invoice);
        redemption.setBooking(booking);
        redemption.setPromoCodeSnapshot(firstNonBlank(promo.getCode(), preview.appliedCode()));
        redemption.setOwnerType(normalizeEnumValue(promo.getOwnerType(), "ADMIN"));
        redemption.setTargetType("BOOKING");
        redemption.setDiscountType(normalizeEnumValue(promo.getDiscountType(), "FIXED_AMOUNT"));
        redemption.setSubtotalAmount(
                defaultMoney(booking != null ? booking.getSubtotalAmount() : invoice.getSubtotalAmount()));
        redemption.setDiscountAmount(defaultMoney(preview.discountAmount()));
        redemption.setRedeemedAt(LocalDateTime.now(APP_ZONE));
        promoCodeRedemptionRepository.save(redemption);
        incrementUsageCount(promo);
    }

    @Override
    @Transactional
    public void recordMembershipRedemption(PromoPreview preview, User user, MembershipSubscription subscription,
            Invoice invoice) {
        if (preview == null || !preview.applied() || invoice == null || invoice.getId() == null || user == null) {
            return;
        }
        if (promoCodeRedemptionRepository.existsByInvoice_IdAndPromoCode_Id(invoice.getId(),
                preview.promoCode().getId())) {
            return;
        }
        PromoCode promo = preview.promoCode();
        PromoCodeRedemption redemption = new PromoCodeRedemption();
        redemption.setPromoCode(promo);
        redemption.setUser(user);
        redemption.setInvoice(invoice);
        redemption.setMembershipSubscription(subscription);
        redemption.setPromoCodeSnapshot(firstNonBlank(promo.getCode(), preview.appliedCode()));
        redemption.setOwnerType(normalizeEnumValue(promo.getOwnerType(), "ADMIN"));
        redemption.setTargetType("MEMBERSHIP");
        redemption.setDiscountType(normalizeEnumValue(promo.getDiscountType(), "FIXED_AMOUNT"));
        redemption.setSubtotalAmount(defaultMoney(invoice.getSubtotalAmount()));
        redemption.setDiscountAmount(defaultMoney(preview.discountAmount()));
        redemption.setRedeemedAt(LocalDateTime.now(APP_ZONE));
        promoCodeRedemptionRepository.save(redemption);
        incrementUsageCount(promo);
    }

    @Override
    public BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal discount, BigDecimal tax) {
        BigDecimal total = defaultMoney(subtotal).subtract(defaultMoney(discount)).add(defaultMoney(tax));
        return total.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : total;
    }

    private PromoCode requireActivePromo(String promoCode) {
        PromoCode promo = promoCodeRepository.findByCodeIgnoreCaseAndActiveTrue(promoCode)
                .orElseThrow(() -> new BadRequestException("Mã ưu đãi không tồn tại hoặc đã tạm dừng"));
        LocalDateTime now = LocalDateTime.now(APP_ZONE);
        if (promo.getStartsAt() != null && now.isBefore(promo.getStartsAt())) {
            throw new BadRequestException("Mã ưu đãi chưa đến thời gian sử dụng");
        }
        if (promo.getEndsAt() != null && now.isAfter(promo.getEndsAt())) {
            throw new BadRequestException("Mã ưu đãi đã hết hạn");
        }
        return promo;
    }

    private void validateCommonPolicy(PromoCode promo, User user, BigDecimal subtotal, String requiredTarget) {
        String targetType = normalizeEnumValue(promo.getTargetType(), "BOTH");
        if (!("BOTH".equals(targetType) || requiredTarget.equals(targetType))) {
            throw new BadRequestException("Mã ưu đãi không áp dụng cho " + targetLabel(requiredTarget));
        }

        BigDecimal minOrder = defaultMoney(promo.getMinOrderAmount());
        if (subtotal.compareTo(minOrder) < 0) {
            throw new BadRequestException("Giá trị thanh toán chưa đạt tối thiểu để dùng ưu đãi");
        }

        Integer totalLimit = promo.getUsageLimitTotal();
        if (totalLimit != null && totalLimit > 0
                && promoCodeRedemptionRepository.countByPromoCode_Id(promo.getId()) >= totalLimit) {
            throw new BadRequestException("Mã ưu đãi đã hết lượt sử dụng");
        }

        Integer perUserLimit = promo.getUsageLimitPerUser();
        if (perUserLimit != null && perUserLimit > 0 && user != null
                && promoCodeRedemptionRepository.countByPromoCode_IdAndUser_Id(promo.getId(),
                        user.getId()) >= perUserLimit) {
            throw new BadRequestException("Bạn đã dùng hết lượt cho mã ưu đãi này");
        }

        validateUserSegment(promo, user);
    }

    private void validateBookingScope(PromoCode promo, Booking booking, User user) {
        String ownerType = normalizeEnumValue(promo.getOwnerType(), "ADMIN");
        Long bookingProviderId = booking.getProvider() != null ? booking.getProvider().getId() : null;
        if ("PARTNER".equals(ownerType)) {
            Long promoProviderId = promo.getProvider() != null ? promo.getProvider().getId() : null;
            if (!Objects.equals(promoProviderId, bookingProviderId)) {
                throw new BadRequestException("Mã ưu đãi của partner khác không áp dụng cho booking này");
            }
        }

        Set<Long> providerIds = parseLongCsv(promo.getProviderIds());
        if (!providerIds.isEmpty() && !providerIds.contains(bookingProviderId)) {
            throw new BadRequestException("Mã ưu đãi không áp dụng cho shop này");
        }

        Long providerServiceId = booking.getProviderService() != null ? booking.getProviderService().getId() : null;
        Set<Long> providerServiceIds = parseLongCsv(promo.getProviderServiceIds());
        if (!providerServiceIds.isEmpty() && !providerServiceIds.contains(providerServiceId)) {
            throw new BadRequestException("Mã ưu đãi không áp dụng cho dịch vụ này");
        }

        Set<Long> categoryIds = parseLongCsv(promo.getServiceCategoryIds());
        if (!categoryIds.isEmpty() && !bookingMatchesCategory(booking.getProviderService(), categoryIds)) {
            throw new BadRequestException("Mã ưu đãi không áp dụng cho nhóm dịch vụ này");
        }

        Set<String> days = parseStringCsv(promo.getApplicableDaysOfWeek());
        if (!days.isEmpty() && booking.getAppointmentDate() != null) {
            DayOfWeek dayOfWeek = booking.getAppointmentDate().getDayOfWeek();
            if (!days.contains(dayOfWeek.name())) {
                throw new BadRequestException("Mã ưu đãi không áp dụng vào ngày đặt lịch này");
            }
        }

        validateCompletedBookingThreshold(promo, user);
    }

    private void validateMembershipScope(PromoCode promo, MembershipPlan plan) {
        if ("PARTNER".equals(normalizeEnumValue(promo.getOwnerType(), "ADMIN"))) {
            throw new BadRequestException("Mã ưu đãi của partner chỉ áp dụng cho booking dịch vụ");
        }
        Set<Long> planIds = parseLongCsv(promo.getMembershipPlanIds());
        if (!planIds.isEmpty() && !planIds.contains(plan.getId())) {
            throw new BadRequestException("Mã ưu đãi không áp dụng cho gói membership này");
        }
    }

    private void validateUserSegment(PromoCode promo, User user) {
        if (user == null || user.getId() == null) {
            return;
        }
        String segment = normalizeEnumValue(promo.getUserSegment(), "ALL");
        long bookingCount = bookingRepository.countByCustomerUser_Id(user.getId());
        long activeMembershipCount = membershipSubscriptionRepository.countByUser_IdAndStatusIn(user.getId(),
                ACTIVE_MEMBERSHIP_STATUSES);

        if ("NEW_USER".equals(segment) && bookingCount > 1) {
            throw new BadRequestException("Mã ưu đãi chỉ dành cho khách hàng mới");
        }
        if ("RETURNING_USER".equals(segment) && bookingCount <= 1) {
            throw new BadRequestException("Mã ưu đãi chỉ dành cho khách hàng đã quay lại sử dụng dịch vụ");
        }
        if ("MEMBERSHIP_ACTIVE".equals(segment) && activeMembershipCount <= 0) {
            throw new BadRequestException("Mã ưu đãi chỉ dành cho hội viên đang hoạt động");
        }
        if ("NON_MEMBER".equals(segment) && activeMembershipCount > 0) {
            throw new BadRequestException("Mã ưu đãi chỉ dành cho khách hàng chưa có membership");
        }
    }

    private void validateCompletedBookingThreshold(PromoCode promo, User user) {
        Integer requiredCompletedBookings = promo.getMinCompletedBookings();
        if (requiredCompletedBookings == null || requiredCompletedBookings <= 0 || user == null
                || user.getId() == null) {
            return;
        }
        long completedBookings = bookingRepository.countByCustomerUser_IdAndStatus(user.getId(), "COMPLETED");
        if (completedBookings < requiredCompletedBookings) {
            throw new BadRequestException("Bạn chưa đạt số booking hoàn thành tối thiểu để dùng ưu đãi");
        }
    }

    private boolean bookingMatchesCategory(ProviderService providerService, Collection<Long> acceptedCategoryIds) {
        if (providerService == null || providerService.getService() == null) {
            return false;
        }
        ServiceCategory cursor = providerService.getService().getCategory();
        while (cursor != null) {
            if (acceptedCategoryIds.contains(cursor.getId())) {
                return true;
            }
            cursor = cursor.getParent();
        }
        return false;
    }

    private BigDecimal calculatePromoDiscount(BigDecimal subtotal, PromoCode promo) {
        BigDecimal safeSubtotal = defaultMoney(subtotal);
        BigDecimal discountValue = defaultMoney(promo.getDiscountValue());
        if (discountValue.compareTo(BigDecimal.ZERO) <= 0 || safeSubtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        String discountType = normalizeEnumValue(promo.getDiscountType(), "FIXED_AMOUNT");
        switch (discountType) {
            case "PERCENTAGE" -> discount = safeSubtotal.multiply(discountValue)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            case "FIXED_PRICE" -> discount = safeSubtotal.subtract(discountValue);
            case "FREE_SERVICE" -> discount = safeSubtotal;
            case "BOGO" -> discount = safeSubtotal.multiply(discountValue)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            default -> discount = discountValue;
        }

        BigDecimal maxDiscount = promo.getMaxDiscountAmount();
        if (maxDiscount != null && maxDiscount.compareTo(BigDecimal.ZERO) > 0 && discount.compareTo(maxDiscount) > 0) {
            discount = maxDiscount;
        }
        if (discount.compareTo(safeSubtotal) > 0) {
            discount = safeSubtotal;
        }
        return discount.max(BigDecimal.ZERO);
    }

    private void incrementUsageCount(PromoCode promo) {
        promo.setUsageCount(defaultInteger(promo.getUsageCount()) + 1);
        promoCodeRepository.save(promo);
    }

    private Set<Long> parseLongCsv(String value) {
        String normalized = normalizeBlank(value);
        if (normalized == null) {
            return Set.of();
        }
        return Arrays.stream(normalized.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .map(item -> {
                    try {
                        return Long.parseLong(item);
                    } catch (NumberFormatException ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private Set<String> parseStringCsv(String value) {
        String normalized = normalizeBlank(value);
        if (normalized == null) {
            return Set.of();
        }
        return Arrays.stream(normalized.split(","))
                .map(item -> item.trim().toUpperCase(Locale.ROOT))
                .filter(item -> !item.isBlank())
                .collect(Collectors.toSet());
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Integer defaultInteger(Integer value) {
        return value == null ? 0 : value;
    }

    private String normalizeEnumValue(String value, String fallback) {
        String normalized = normalizeBlank(value);
        return normalized == null ? fallback
                : normalized.trim().replace('-', '_').replace(' ', '_').toUpperCase(Locale.ROOT);
    }

    private String normalizeBlank(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    private String targetLabel(String targetType) {
        return "MEMBERSHIP".equals(targetType) ? "membership" : "booking";
    }
}