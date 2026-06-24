package com.example.petgo.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record AreaScheduleOverrideResponse(
        Long id,
        Long areaId,
        LocalDate overrideDate,
        LocalTime openTime,
        LocalTime closeTime,
        Boolean closed,
        String reason) {
}
