package com.example.petgo.dto.partner;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PartnerProfileResponse(
        Long providerId,
        Long userId,
        String providerCode,
        String businessName,
        String slug,
        String providerType,
        String headline,
        String description,
        Integer yearsExperience,
        String verificationStatus,
        Boolean acceptsInstantBooking,
        Boolean acceptsMembership,
        BigDecimal averageRating,
        Integer totalReviews,
        Integer totalCompletedBookings,
        Integer cancellationFreeHours,
        String emergencyPhone,
        String primaryAddressLine1,
        String primaryAddressLine2,
        String ward,
        String district,
        String city,
        String province,
        String countryCode,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        String mainImageUrl,
        String coverImageUrl,
        BigDecimal priceFromAmount,
        String currencyCode,
        String status,
        List<String> photoUrls,
        List<PartnerServiceCategoryResponse> registeredCategories,
        String approvedAt) {
}