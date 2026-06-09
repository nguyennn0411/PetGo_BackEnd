package com.example.petgo.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record BookingAvailabilityResponse(
        Long providerId,
        Long providerServiceId,
        String date,
        String timezone,
        Integer durationMinutes,
        Integer bufferAfterMinutes,
        Integer maxConcurrent,
        String status,
        String reason,
        List<BookingSlotOptionResponse> slots) {
}