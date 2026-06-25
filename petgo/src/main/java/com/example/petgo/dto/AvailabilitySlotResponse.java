package com.example.petgo.dto;

import lombok.Builder;

import java.time.LocalTime;

@Builder
public record AvailabilitySlotResponse(
        LocalTime startTime,
        LocalTime endTime,
        String status,
        String reason) {
}
