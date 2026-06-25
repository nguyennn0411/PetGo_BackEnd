package com.example.petgo.service.impl;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.dto.promotion.PromotionOptionsResponse;
import com.example.petgo.dto.promotion.PromotionRequest;
import com.example.petgo.dto.promotion.PromotionResponse;
import com.example.petgo.entity.Area;
import com.example.petgo.entity.MembershipPlan;
import com.example.petgo.entity.PromoCode;
import com.example.petgo.entity.RoleType;
import com.example.petgo.entity.ServiceCategory;
import com.example.petgo.entity.User;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.exception.UnauthorizedException;
import com.example.petgo.repository.AreaRepository;
import com.example.petgo.repository.MembershipPlanRepository;
import com.example.petgo.repository.PromoCodeRedemptionRepository;
import com.example.petgo.repository.PromoCodeRepository;
import com.example.petgo.repository.ServiceCategoryRepository;
import com.example.petgo.repository.UserRepository;
import com.example.petgo.repository.UserRoleRepository;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.PromotionService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final Set<String> PROMOTION_TYPES = Set.of(
            "PROMO_CODE", "FLASH_SALE", "FIRST_BOOKING", "LOYALTY", "MEMBERSHIP", "SEASONAL", "BUNDLE",
            "FREE_SERVICE");
    private static final Set<String> ADMIN_TARGET_TYPES = Set.of("BOOKING", "SHIPPING", "MEMBERSHIP", "BOTH");
    private static final Set<String> DISCOUNT_TYPES = Set.of(
            "PERCENTAGE", "FIXED_AMOUNT", "FIXED_PRICE", "FREE_SERVICE", "BOGO");
    private static final Set<String> USER_SEGMENTS = Set.of(
            "ALL", "NEW_USER", "RETURNING_USER", "MEMBERSHIP_ACTIVE", "NON_MEMBER");
    private static final List<String> DAYS_OF_WEEK = List.of(
            "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");

    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeRedemptionRepository promoCodeRedemptionRepository;
    private final AreaRepository areaRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponse> listAdminPromotions(HttpServletRequest request, String status, String targetType) {
        requireAdmin(request);
        String statusFilter = normalizeOptionalEnum(status);
        String targetFilter = normalizeOptionalEnum(targetType);
        return promoCodeRepository.findAllByOrderByCreatedAtDescIdDesc().stream()
                .filter(promo -> "ADMIN".equals(normalizeOwnerType(promo)))
                .filter(promo -> targetFilter == null || targetFilter.equals(normalizeTargetTypeValue(promo)))
                .filter(promo -> statusFilter == null || statusFilter.equals(resolveStatus(promo)))
                .map(this::mapResponse)
                .toList();
    }

    @Override
    @Transactional
    public PromotionResponse createAdminPromotion(HttpServletRequest request, PromotionRequest requestBody) {
        User admin = requireAdmin(request);
        PromoCode promo = new PromoCode();
        promo.setCreatedByUser(admin);
        promo.setOwnerType("ADMIN");
        applyMutableFields(promo, requestBody, true);
        return mapResponse(promoCodeRepository.save(promo));
    }

    @Override
    @Transactional
    public PromotionResponse updateAdminPromotion(HttpServletRequest request, Long id, PromotionRequest requestBody) {
        requireAdmin(request);
        PromoCode promo = promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ưu đãi."));
        applyMutableFields(promo, requestBody, false);
        return mapResponse(promoCodeRepository.save(promo));
    }

    @Override
    @Transactional
    public PromotionResponse updateAdminPromotionStatus(HttpServletRequest request, Long id, Boolean active) {
        requireAdmin(request);
        PromoCode promo = promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ưu đãi."));
        promo.setActive(Boolean.TRUE.equals(active));
        return mapResponse(promoCodeRepository.save(promo));
    }

    @Override
    @Transactional
    public void deleteAdminPromotion(HttpServletRequest request, Long id) {
        requireAdmin(request);
        PromoCode promo = promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ưu đãi."));
        long redemptionCount = promoCodeRedemptionRepository.countByPromoCode_Id(id);
        if (redemptionCount > 0) {
            throw new BadRequestException("Không thể xóa mã đã có lượt sử dụng. Hãy tắt mã thay vì xóa.");
        }
        promoCodeRepository.delete(promo);
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionOptionsResponse getAdminOptions(HttpServletRequest request) {
        requireAdmin(request);
        return buildOptions();
    }

    private void applyMutableFields(PromoCode promo, PromotionRequest requestBody, boolean creating) {
        if (requestBody == null) {
            throw new BadRequestException("Thiếu thông tin ưu đãi.");
        }
        String code = normalizeCode(requestBody.code());
        ensureUniqueCode(code, promo.getId());

        String promotionType = normalizeRequiredEnum(requestBody.promotionType(), "PROMO_CODE", PROMOTION_TYPES,
                "Loại chương trình ưu đãi không hợp lệ.");
        String targetType = normalizeRequiredEnum(requestBody.targetType(), "MEMBERSHIP", ADMIN_TARGET_TYPES,
                "Phạm vi áp dụng ưu đãi không hợp lệ.");
        String discountType = normalizeRequiredEnum(requestBody.discountType(), "FIXED_AMOUNT", DISCOUNT_TYPES,
                "Kiểu giảm giá không hợp lệ.");
        String userSegment = normalizeRequiredEnum(requestBody.userSegment(), "ALL", USER_SEGMENTS,
                "Nhóm khách hàng áp dụng không hợp lệ.");

        validateMoneyPolicy(requestBody.discountValue(), requestBody.maxDiscountAmount(), requestBody.minOrderAmount(),
                discountType);
        validateLimits(requestBody.usageLimitTotal(), requestBody.usageLimitPerUser(),
                requestBody.minCompletedBookings(),
                requestBody.priority());
        validateDates(requestBody.startsAt(), requestBody.endsAt(), promo, creating);

        List<String> days = normalizeDays(requestBody.applicableDaysOfWeek());
        List<Long> areaIds = normalizeLongList(requestBody.areaIds());
        List<Long> serviceCategoryIds = normalizeLongList(requestBody.serviceCategoryIds());
        List<Long> membershipPlanIds = normalizeLongList(requestBody.membershipPlanIds());
        validateAreaIds(areaIds);

        promo.setCode(code);
        promo.setName(normalizeRequiredText(requestBody.name(), "Tên ưu đãi không được để trống."));
        promo.setDescription(normalizeBlank(requestBody.description()));
        promo.setOwnerType("ADMIN");
        promo.setPromotionType(promotionType);
        promo.setTargetType(targetType);
        promo.setDiscountType(discountType);
        promo.setDiscountValue(requestBody.discountValue());
        promo.setMaxDiscountAmount(normalizeOptionalMoney(requestBody.maxDiscountAmount()));
        promo.setMinOrderAmount(normalizeOptionalMoney(requestBody.minOrderAmount()));
        promo.setUsageLimitTotal(normalizePositiveInteger(requestBody.usageLimitTotal()));
        promo.setUsageLimitPerUser(normalizePositiveInteger(requestBody.usageLimitPerUser()));
        promo.setUsageCount(creating ? 0 : defaultInteger(promo.getUsageCount()));
        promo.setStackable(Boolean.TRUE.equals(requestBody.stackable()));
        promo.setAutoApply(Boolean.TRUE.equals(requestBody.autoApply()));
        promo.setPriority(requestBody.priority() == null ? 0 : requestBody.priority());
        promo.setUserSegment(userSegment);
        promo.setMinCompletedBookings(normalizePositiveInteger(requestBody.minCompletedBookings()));
        promo.setApplicableDaysOfWeek(joinStrings(days));
        promo.setAreaIds(joinLongs(areaIds));
        promo.setServiceCategoryIds(joinLongs(serviceCategoryIds));
        promo.setMembershipPlanIds(joinLongs(membershipPlanIds));
        promo.setBadgeText(normalizeBlank(requestBody.badgeText()));
        promo.setLandingPageUrl(normalizeBlank(requestBody.landingPageUrl()));
        promo.setTermsAndConditions(normalizeBlank(requestBody.termsAndConditions()));
        promo.setInternalNote(normalizeBlank(requestBody.internalNote()));
        promo.setStartsAt(requestBody.startsAt());
        promo.setEndsAt(requestBody.endsAt());
        promo.setActive(requestBody.active() == null || Boolean.TRUE.equals(requestBody.active()));
    }

    private PromotionOptionsResponse buildOptions() {
        return PromotionOptionsResponse.builder()
                .promotionTypes(List.of(
                        option("PROMO_CODE", "Mã ưu đãi", "Khách nhập mã khi checkout."),
                        option("FLASH_SALE", "Flash sale", "Ưu đãi thời gian ngắn, ưu tiên cao."),
                        option("FIRST_BOOKING", "Khách mới", "Ưu đãi cho booking đầu hoặc user mới."),
                        option("LOYALTY", "Khách thân thiết", "Ưu đãi cho khách đã quay lại nhiều lần."),
                        option("MEMBERSHIP", "Membership", "Ưu đãi gói hội viên hoặc hội viên hiện hữu."),
                        option("SEASONAL", "Theo mùa/sự kiện", "Ưu đãi dịp lễ, cuối tuần, chiến dịch."),
                        option("BUNDLE", "Combo/bundle", "Ưu đãi theo nhóm dịch vụ hoặc gói."),
                        option("FREE_SERVICE", "Tặng dịch vụ", "Có thể cấu hình miễn phí/toàn phần.")))
                .targetTypes(List.of(
                        option("BOOKING", "Booking dịch vụ", "Áp dụng khi khách đặt dịch vụ vận chuyển."),
                        option("SHIPPING", "Phí vận chuyển", "Áp dụng giảm trên phí vận chuyển."),
                        option("MEMBERSHIP", "Membership", "Áp dụng khi khách mua gói hội viên."),
                        option("BOTH", "Cả hai", "Áp dụng cho booking và membership nếu thỏa điều kiện.")))
                .discountTypes(List.of(
                        option("PERCENTAGE", "Giảm theo %", "Ví dụ 15%, có thể kèm mức giảm tối đa."),
                        option("FIXED_AMOUNT", "Giảm số tiền", "Ví dụ giảm 50.000đ."),
                        option("FIXED_PRICE", "Giá sau giảm", "Đưa tổng thanh toán về mức giá cố định."),
                        option("FREE_SERVICE", "Miễn phí", "Giảm tối đa bằng giá trị thanh toán."),
                        option("BOGO", "Combo/BOGO", "Dùng giá trị % để mô phỏng mua kèm/tặng kèm.")))
                .userSegments(List.of(
                        option("ALL", "Tất cả khách hàng", "Không giới hạn theo lịch sử mua."),
                        option("NEW_USER", "Khách mới", "Chỉ áp dụng cho khách chưa có nhiều booking."),
                        option("RETURNING_USER", "Khách quay lại", "Áp dụng cho khách đã từng sử dụng PetGo."),
                        option("MEMBERSHIP_ACTIVE", "Hội viên active", "Chỉ áp dụng cho user có membership active."),
                        option("NON_MEMBER", "Chưa là hội viên", "Chỉ áp dụng cho user chưa có membership active.")))
                .daysOfWeek(DAYS_OF_WEEK.stream()
                        .map(day -> option(day, dayLabel(day), "Áp dụng vào " + dayLabel(day).toLowerCase(Locale.ROOT)))
                        .toList())
                .areas(areaRepository.findAllByOrderByNameAsc().stream()
                        .map(this::mapAreaOption)
                        .toList())
                .serviceCategories(serviceCategoryRepository.findByActiveTrueOrderByNameAscIdAsc().stream()
                        .map(this::mapCategoryOption)
                        .toList())
                .membershipPlans(membershipPlanRepository.findByActiveTrueOrderByPopularDescSortOrderAscIdAsc().stream()
                        .map(plan -> PromotionOptionsResponse.MembershipPlanOption.builder()
                                .id(plan.getId())
                                .name(plan.getName())
                                .slug(plan.getSlug())
                                .billingCycle(plan.getBillingCycle())
                                .build())
                        .toList())
                .build();
    }

    private PromotionResponse mapResponse(PromoCode promo) {
        int redemptionCount = (int) promoCodeRedemptionRepository.countByPromoCode_Id(promo.getId());
        int usageCount = Math.max(defaultInteger(promo.getUsageCount()), redemptionCount);
        User createdBy = promo.getCreatedByUser();
        return PromotionResponse.builder()
                .id(promo.getId())
                .code(promo.getCode())
                .name(firstNonBlank(promo.getName(), promo.getCode()))
                .description(promo.getDescription())
                .ownerType(normalizeOwnerType(promo))
                .createdByUserId(createdBy != null ? createdBy.getId() : null)
                .createdByName(createdBy != null ? createdBy.getFullName() : null)
                .promotionType(normalizeEnum(promo.getPromotionType(), "PROMO_CODE"))
                .targetType(normalizeTargetTypeValue(promo))
                .discountType(normalizeEnum(promo.getDiscountType(), "FIXED_AMOUNT"))
                .discountValue(defaultMoney(promo.getDiscountValue()))
                .maxDiscountAmount(defaultMoney(promo.getMaxDiscountAmount()))
                .minOrderAmount(defaultMoney(promo.getMinOrderAmount()))
                .usageLimitTotal(promo.getUsageLimitTotal())
                .usageLimitPerUser(promo.getUsageLimitPerUser())
                .usageCount(usageCount)
                .stackable(Boolean.TRUE.equals(promo.getStackable()))
                .autoApply(Boolean.TRUE.equals(promo.getAutoApply()))
                .priority(defaultInteger(promo.getPriority()))
                .userSegment(normalizeEnum(promo.getUserSegment(), "ALL"))
                .minCompletedBookings(promo.getMinCompletedBookings())
                .applicableDaysOfWeek(parseStringCsv(promo.getApplicableDaysOfWeek()))
                .areaIds(parseLongCsv(promo.getAreaIds()))
                .serviceCategoryIds(parseLongCsv(promo.getServiceCategoryIds()))
                .membershipPlanIds(parseLongCsv(promo.getMembershipPlanIds()))
                .badgeText(promo.getBadgeText())
                .landingPageUrl(promo.getLandingPageUrl())
                .termsAndConditions(promo.getTermsAndConditions())
                .internalNote(promo.getInternalNote())
                .startsAt(promo.getStartsAt())
                .endsAt(promo.getEndsAt())
                .active(Boolean.TRUE.equals(promo.getActive()))
                .status(resolveStatus(promo))
                .discountSummary(buildDiscountSummary(promo))
                .scopeSummary(buildScopeSummary(promo))
                .createdAt(promo.getCreatedAt())
                .updatedAt(promo.getUpdatedAt())
                .build();
    }

    private PromotionOptionsResponse.OptionItem option(String value, String label, String description) {
        return PromotionOptionsResponse.OptionItem.builder()
                .value(value)
                .label(label)
                .description(description)
                .build();
    }

    private PromotionOptionsResponse.AreaOption mapAreaOption(Area area) {
        return PromotionOptionsResponse.AreaOption.builder()
                .id(area.getId())
                .name(area.getName())
                .pickupAddress(area.getPickupAddress())
                .build();
    }

    private PromotionOptionsResponse.ServiceCategoryOption mapCategoryOption(ServiceCategory category) {
        ServiceCategory parent = category.getParent();
        return PromotionOptionsResponse.ServiceCategoryOption.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(parent != null ? parent.getId() : null)
                .parentName(parent != null ? parent.getName() : null)
                .build();
    }

    private User requireAdmin(HttpServletRequest request) {
        AuthenticatedUser authenticatedUser = authService.requireAccessUser(request);
        User user = userRepository.findById(authenticatedUser.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng."));
        boolean isAdmin = userRoleRepository.findByUser_Id(user.getId()).stream()
                .anyMatch(userRole -> userRole.getRole() != null
                        && RoleType.ADMIN.equals(userRole.getRole().getCode()));
        if (!isAdmin) {
            throw new UnauthorizedException("Bạn không có quyền admin.");
        }
        return user;
    }

    private void validateMoneyPolicy(BigDecimal discountValue, BigDecimal maxDiscountAmount, BigDecimal minOrderAmount,
            String discountType) {
        if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Giá trị giảm giá phải lớn hơn 0.");
        }
        if (("PERCENTAGE".equals(discountType) || "BOGO".equals(discountType))
                && discountValue.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BadRequestException("Giảm theo phần trăm không được vượt quá 100%.");
        }
        if (maxDiscountAmount != null && maxDiscountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Mức giảm tối đa phải >= 0.");
        }
        if (minOrderAmount != null && minOrderAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Đơn tối thiểu phải >= 0.");
        }
    }

    private void validateLimits(Integer usageLimitTotal, Integer usageLimitPerUser, Integer minCompletedBookings,
            Integer priority) {
        if (usageLimitTotal != null && usageLimitTotal < 0) {
            throw new BadRequestException("Tổng lượt dùng phải >= 0.");
        }
        if (usageLimitPerUser != null && usageLimitPerUser < 0) {
            throw new BadRequestException("Lượt dùng mỗi khách phải >= 0.");
        }
        if (usageLimitTotal != null && usageLimitTotal > 0
                && usageLimitPerUser != null && usageLimitPerUser > usageLimitTotal) {
            throw new BadRequestException("Lượt dùng mỗi khách không được lớn hơn tổng lượt dùng.");
        }
        if (minCompletedBookings != null && minCompletedBookings < 0) {
            throw new BadRequestException("Số booking hoàn thành tối thiểu phải >= 0.");
        }
        if (priority != null && priority < 0) {
            throw new BadRequestException("Độ ưu tiên phải >= 0.");
        }
    }

    private void validateDates(LocalDateTime startsAt, LocalDateTime endsAt, PromoCode existingPromo,
            boolean creating) {
        LocalDateTime now = LocalDateTime.now(APP_ZONE).truncatedTo(ChronoUnit.MINUTES);
        boolean startChanged = creating || !Objects.equals(startsAt, existingPromo.getStartsAt());
        boolean endChanged = creating || !Objects.equals(endsAt, existingPromo.getEndsAt());

        if (startsAt != null && startChanged && startsAt.isBefore(now)) {
            throw new BadRequestException("Thời điểm bắt đầu khuyến mãi không được nằm trong quá khứ.");
        }
        if (endsAt != null && endChanged && endsAt.isBefore(now)) {
            throw new BadRequestException("Thời điểm kết thúc khuyến mãi không được nằm trong quá khứ.");
        }
        if (startsAt != null && endsAt != null && !endsAt.isAfter(startsAt)) {
            throw new BadRequestException("Thời điểm kết thúc phải sau thời điểm bắt đầu.");
        }
    }

    private String normalizeCode(String value) {
        String code = normalizeRequiredText(value, "Mã ưu đãi không được để trống.")
                .trim()
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);
        if (!code.matches("[A-Z0-9_-]{3,50}")) {
            throw new BadRequestException("Mã ưu đãi chỉ gồm chữ, số, dấu _ hoặc -, từ 3 đến 50 ký tự.");
        }
        return code;
    }

    private String normalizeRequiredEnum(String value, String fallback, Set<String> acceptedValues,
            String errorMessage) {
        String normalized = normalizeEnum(value, fallback);
        if (!acceptedValues.contains(normalized)) {
            throw new BadRequestException(errorMessage);
        }
        return normalized;
    }

    private String normalizeOptionalEnum(String value) {
        String normalized = normalizeBlank(value);
        if (normalized == null || "ALL".equalsIgnoreCase(normalized)) {
            return null;
        }
        return normalizeEnum(normalized, null);
    }

    private String normalizeEnum(String value, String fallback) {
        String normalized = normalizeBlank(value);
        if (normalized == null) {
            return fallback;
        }
        return normalized.replace('-', '_').replace(' ', '_').toUpperCase(Locale.ROOT);
    }

    private String normalizeRequiredText(String value, String errorMessage) {
        String normalized = normalizeBlank(value);
        if (normalized == null) {
            throw new BadRequestException(errorMessage);
        }
        return normalized;
    }

    private String normalizeBlank(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private List<String> normalizeDays(List<String> values) {
        if (values == null) {
            return List.of();
        }
        List<String> days = values.stream()
                .map(value -> normalizeEnum(value, null))
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        boolean invalid = days.stream().anyMatch(day -> !DAYS_OF_WEEK.contains(day));
        if (invalid) {
            throw new BadRequestException("Ngày áp dụng ưu đãi không hợp lệ.");
        }
        return days;
    }

    private List<Long> normalizeLongList(List<Long> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .filter(Objects::nonNull)
                .filter(value -> value > 0)
                .distinct()
                .toList();
    }

    private Integer normalizePositiveInteger(Integer value) {
        return value == null || value <= 0 ? null : value;
    }

    private BigDecimal normalizeOptionalMoney(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) <= 0 ? null : value;
    }

    private String joinLongs(List<Long> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    private String joinStrings(List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return String.join(",", values);
    }

    private List<Long> parseLongCsv(String value) {
        String normalized = normalizeBlank(value);
        if (normalized == null) {
            return List.of();
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
                .distinct()
                .toList();
    }

    private List<String> parseStringCsv(String value) {
        String normalized = normalizeBlank(value);
        if (normalized == null) {
            return List.of();
        }
        return Arrays.stream(normalized.split(","))
                .map(item -> normalizeEnum(item, null))
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private String normalizeOwnerType(PromoCode promo) {
        return "ADMIN";
    }

    private String normalizeTargetTypeValue(PromoCode promo) {
        return normalizeEnum(promo.getTargetType(), "BOTH");
    }

    private String resolveStatus(PromoCode promo) {
        if (!Boolean.TRUE.equals(promo.getActive())) {
            return "INACTIVE";
        }
        LocalDateTime now = LocalDateTime.now(APP_ZONE);
        if (promo.getStartsAt() != null && now.isBefore(promo.getStartsAt())) {
            return "SCHEDULED";
        }
        if (promo.getEndsAt() != null && now.isAfter(promo.getEndsAt())) {
            return "EXPIRED";
        }
        Integer limit = promo.getUsageLimitTotal();
        if (limit != null && limit > 0 && promoCodeRedemptionRepository.countByPromoCode_Id(promo.getId()) >= limit) {
            return "USED_UP";
        }
        return "ACTIVE";
    }

    private String buildDiscountSummary(PromoCode promo) {
        String discountType = normalizeEnum(promo.getDiscountType(), "FIXED_AMOUNT");
        BigDecimal value = defaultMoney(promo.getDiscountValue());
        return switch (discountType) {
            case "PERCENTAGE" -> value.stripTrailingZeros().toPlainString() + "%"
                    + (promo.getMaxDiscountAmount() != null ? " tối đa " + formatMoney(promo.getMaxDiscountAmount())
                            : "");
            case "FIXED_PRICE" -> "Giá còn " + formatMoney(value);
            case "FREE_SERVICE" -> "Miễn phí dịch vụ";
            case "BOGO" -> "Combo/BOGO " + value.stripTrailingZeros().toPlainString() + "%";
            default -> "Giảm " + formatMoney(value);
        };
    }

    private String buildScopeSummary(PromoCode promo) {
        List<String> parts = new ArrayList<>();
        parts.add("Do admin đặt");
        parts.add("Target: " + normalizeTargetTypeValue(promo));
        if (promo.getMinOrderAmount() != null && promo.getMinOrderAmount().compareTo(BigDecimal.ZERO) > 0) {
            parts.add("Đơn tối thiểu " + formatMoney(promo.getMinOrderAmount()));
        }
        if (!parseStringCsv(promo.getApplicableDaysOfWeek()).isEmpty()) {
            parts.add("Theo ngày trong tuần");
        }
        if (!parseLongCsv(promo.getAreaIds()).isEmpty()) {
            parts.add("Giới hạn khu vực");
        }
        if (!parseLongCsv(promo.getServiceCategoryIds()).isEmpty()) {
            parts.add("Giới hạn nhóm dịch vụ");
        }
        if (!parseLongCsv(promo.getMembershipPlanIds()).isEmpty()) {
            parts.add("Giới hạn gói membership");
        }
        return String.join(" • ", parts);
    }

    private String dayLabel(String day) {
        return switch (day) {
            case "MONDAY" -> "Thứ hai";
            case "TUESDAY" -> "Thứ ba";
            case "WEDNESDAY" -> "Thứ tư";
            case "THURSDAY" -> "Thứ năm";
            case "FRIDAY" -> "Thứ sáu";
            case "SATURDAY" -> "Thứ bảy";
            case "SUNDAY" -> "Chủ nhật";
            default -> day;
        };
    }

    private String formatMoney(BigDecimal amount) {
        return String.format(Locale.forLanguageTag("vi-VN"), "%,.0fđ", defaultMoney(amount));
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Integer defaultInteger(Integer value) {
        return value == null ? 0 : value;
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

    private void validateAreaIds(List<Long> areaIds) {
        if (areaIds == null || areaIds.isEmpty()) {
            return;
        }
        List<Area> areas = areaRepository.findAllById(areaIds);
        if (areas.size() != areaIds.size()) {
            throw new BadRequestException("Có khu vực không tồn tại trong danh sách giới hạn.");
        }
    }

    private void ensureUniqueCode(String code, Long excludeId) {
        if (code == null) return;
        if (promoCodeRepository.existsByCodeIgnoreCase(code)) {
            if (excludeId != null) {
                promoCodeRepository.findByCodeIgnoreCase(code)
                        .filter(existing -> !existing.getId().equals(excludeId))
                        .ifPresent(existing -> { throw new BadRequestException("Mã ưu đãi đã tồn tại."); });
            } else {
                throw new BadRequestException("Mã ưu đãi đã tồn tại.");
            }
        }
    }
}