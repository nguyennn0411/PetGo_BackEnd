package com.example.petgo.dto.partner;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PartnerDashboardSummaryResponse(
        Long providerId,
        String businessName,
        String verificationStatus,
        String status,
        long todayBookings,
        long pendingBookings,
        long upcomingBookings,
        long completedBookings,
        long cancelledBookings,
        BigDecimal monthlyRevenue,
        String monthlyRevenueDisplay,
        BigDecimal averageRating,
        long newReviews,
        BigDecimal completionRate,
        List<PartnerTopServiceResponse> topServices,
        boolean missingProfileInfo,
        boolean missingServices,
        boolean missingSchedule,
        List<String> warnings,
        List<PartnerBookingSummaryResponse> actionRequiredBookings) {
}