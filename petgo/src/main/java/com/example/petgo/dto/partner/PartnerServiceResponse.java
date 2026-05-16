package com.example.petgo.dto.partner;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PartnerServiceResponse(
        Long id,
        Long providerId,
        Long serviceId,
        String serviceName,
        String customName,
        String displayName,
        String shortDescription,
        String description,
        Integer durationMinutes,
        BigDecimal priceAmount,
        String priceDisplay,
        String currencyCode,
        String priceUnit,
        Boolean featured,
        Boolean active,
        Integer capacityPerSlot,
        Integer bookingBufferMinutes,
        Integer displayOrder,
        Long categoryId,
        String categoryName,
        Long parentCategoryId,
        String parentCategoryName,
        List<Long> categoryIds,
        List<PartnerServiceCategoryResponse> categories,
        List<String> photoUrls,
        String approvalStatus,
        long bookingCount) {
}