package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ProviderCardResponse(
        Long id,
        String name,
        String slug,
        String headline,
        String providerType,
        BigDecimal rating,
        Integer totalReviews,
        String address,
        String city,
        String province,
        BigDecimal priceFrom,
        String currencyCode,
        String distance,
        Double distanceKm,
        String image,
        String featuredService,
        List<String> availableSlots,
        Boolean hot,
        Boolean featured,
        Boolean instantBooking,
        Boolean openNow,
        String verificationStatus,
        List<String> categorySlugs
) {
}
