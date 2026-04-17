package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProviderDetailServiceItemResponse(
        Long id,
        String name,
        String desc,
        BigDecimal price,
        String priceDisplay,
        String currencyCode,
        String priceUnit,
        Integer durationMinutes,
        String duration,
        Boolean featured,
        String categoryName,
        String categorySlug
) {
}
