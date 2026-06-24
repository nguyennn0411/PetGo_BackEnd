package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record HomeProviderResponse(
        Long id,
        String name,
        String slug,
        BigDecimal rating,
        Integer totalReviews,
        String distance,
        BigDecimal price,
        String currencyCode,
        String image,
        String headline,
        Boolean featured,
        Boolean instantBooking,
        String city
) {
}
