package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BookingServiceOptionResponse(
        Long id,
        Long serviceId,
        String name,
        String description,
        Integer durationMinutes,
        String durationLabel,
        BigDecimal priceAmount,
        String priceDisplay,
        String currencyCode,
        String priceUnit,
        Boolean featured,
        String categoryName,
        String categorySlug
) {
}
