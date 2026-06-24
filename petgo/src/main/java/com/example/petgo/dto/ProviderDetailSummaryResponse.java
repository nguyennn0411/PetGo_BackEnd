package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProviderDetailSummaryResponse(
        BigDecimal priceFrom,
        String currencyCode,
        Integer cancellationFreeHours,
        Boolean openNow,
        String distance,
        Double distanceKm,
        Integer totalServices,
        Integer totalGalleryImages,
        Integer totalReviews,
        Integer totalCompletedBookings,
        String nextAvailableSlot
) {
}
