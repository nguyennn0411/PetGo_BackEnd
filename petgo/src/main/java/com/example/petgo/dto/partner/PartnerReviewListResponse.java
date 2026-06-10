package com.example.petgo.dto.partner;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Builder
public record PartnerReviewListResponse(
        Long providerId,
        String businessName,
        BigDecimal averageRating,
        String averageRatingDisplay,
        long totalReviews,
        Map<Integer, Long> ratingDistribution,
        Integer filterRating,
        Long filterServiceId,
        String keyword,
        String fromDate,
        String toDate,
        int page,
        int size,
        long totalItems,
        int totalPages,
        List<PartnerReviewSummaryResponse> reviews) {
}