package com.example.petgo.dto;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record BookingListResponse(
        Long ownerUserId,
        String filterStatus,
        Map<String, Long> counts,
        List<BookingListItemResponse> bookings
) {
}
