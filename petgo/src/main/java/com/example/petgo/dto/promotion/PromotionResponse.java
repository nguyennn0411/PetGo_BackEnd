package com.example.petgo.dto.promotion;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PromotionResponse(
        Long id,
        String code,
        String name,
        String description,
        String ownerType,
        Long providerId,
        String providerName,
        Long createdByUserId,
        String createdByName,
        String promotionType,
        String targetType,
        String discountType,
        BigDecimal discountValue,
        BigDecimal maxDiscountAmount,
        BigDecimal minOrderAmount,
        Integer usageLimitTotal,
        Integer usageLimitPerUser,
        Integer usageCount,
        Boolean stackable,
        Boolean autoApply,
        Integer priority,
        String userSegment,
        Integer minCompletedBookings,
        List<String> applicableDaysOfWeek,
        List<Long> providerIds,
        List<Long> providerServiceIds,
        List<Long> serviceCategoryIds,
        List<Long> membershipPlanIds,
        String badgeText,
        String landingPageUrl,
        String termsAndConditions,
        String internalNote,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        Boolean active,
        String status,
        String discountSummary,
        String scopeSummary,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}