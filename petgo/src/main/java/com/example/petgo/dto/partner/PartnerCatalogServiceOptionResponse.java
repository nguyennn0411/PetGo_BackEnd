package com.example.petgo.dto.partner;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PartnerCatalogServiceOptionResponse(
        Long serviceId,
        String serviceName,
        Long categoryId,
        String categoryName,
        Long parentCategoryId,
        String parentCategoryName,
        Integer defaultDurationMinutes,
        BigDecimal basePriceAmount,
        String priceDisplay,
        String currencyCode,
        String priceUnit,
        String shortDescription) {
}