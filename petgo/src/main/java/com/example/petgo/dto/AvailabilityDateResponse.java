package com.example.petgo.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AvailabilityDateResponse(
        LocalDate date,
        String status,
        String reason) {
}
