package com.example.petgo.dto.promotion;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PromotionRequest(
        @NotBlank(message = "Mã ưu đãi không được để trống.") @Size(max = 50, message = "Mã ưu đãi không được vượt quá 50 ký tự.") String code,

        @NotBlank(message = "Tên ưu đãi không được để trống.") @Size(max = 120, message = "Tên ưu đãi không được vượt quá 120 ký tự.") String name,

        @Size(max = 4000, message = "Mô tả ưu đãi không được vượt quá 4000 ký tự.") String description,

        String promotionType,
        String targetType,
        String discountType,

        @NotNull(message = "Giá trị giảm giá là bắt buộc.") @DecimalMin(value = "0.0", inclusive = false, message = "Giá trị giảm giá phải lớn hơn 0.") BigDecimal discountValue,

        @DecimalMin(value = "0.0", inclusive = true, message = "Mức giảm tối đa phải >= 0.") BigDecimal maxDiscountAmount,

        @DecimalMin(value = "0.0", inclusive = true, message = "Đơn tối thiểu phải >= 0.") BigDecimal minOrderAmount,

        Integer usageLimitTotal,
        Integer usageLimitPerUser,
        Boolean stackable,
        Boolean autoApply,
        Integer priority,
        String userSegment,
        Integer minCompletedBookings,
        List<String> applicableDaysOfWeek,
        List<Long> areaIds,
        List<Long> serviceCategoryIds,
        List<Long> membershipPlanIds,

        @Size(max = 80, message = "Nhãn hiển thị không được vượt quá 80 ký tự.") String badgeText,

        @Size(max = 500, message = "Landing page URL không được vượt quá 500 ký tự.") String landingPageUrl,

        @Size(max = 4000, message = "Điều khoản không được vượt quá 4000 ký tự.") String termsAndConditions,

        @Size(max = 4000, message = "Ghi chú nội bộ không được vượt quá 4000 ký tự.") String internalNote,

        LocalDateTime startsAt,
        LocalDateTime endsAt,
        Boolean active) {
}
