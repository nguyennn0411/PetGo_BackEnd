package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ProviderDetailResponse(
        Long id,
        String slug,
        String name,
        String headline,
        String description,
        String providerType,
        String verificationStatus,
        Boolean featured,
        Boolean hot,
        Boolean instantBooking,
        Boolean acceptsMembership,
        Integer yearsExperience,
        BigDecimal rating,
        Integer reviewsCount,
        String address,
        String city,
        String province,
        String emergencyPhone,
        String bannerImage,
        String mainImage,
        List<String> gallery,
        List<ProviderDetailServiceItemResponse> services,
        List<ProviderBusinessHourDetailResponse> hours,
        List<ProviderSlotResponse> slots,
        List<ProviderReviewResponse> reviews,
        ProviderDetailSummaryResponse summary
) {
}
