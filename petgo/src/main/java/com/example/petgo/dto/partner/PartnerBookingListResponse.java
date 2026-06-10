package com.example.petgo.dto.partner;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record PartnerBookingListResponse(
        Long providerId,
        String filterStatus,
        Map<String, Long> counts,
        List<PartnerBookingSummaryResponse> bookings) {
}