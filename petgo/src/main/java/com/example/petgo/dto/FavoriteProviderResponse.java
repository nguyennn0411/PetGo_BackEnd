package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record FavoriteProviderResponse(
                Long providerId,
                String name,
                String headline,
                BigDecimal rating,
                Integer totalReviews,
                String address,
                BigDecimal priceFrom,
                String priceFromDisplay,
                String currencyCode,
                String image,
                String featuredService,
                String distance,
                Double distanceKm,
                boolean featured,
                boolean hot,
                boolean instantBooking,
                boolean openNow,
                String verificationStatus,
                List<Long> categoryIds,
                List<String> categoryNames,
                String savedAt) {
}
