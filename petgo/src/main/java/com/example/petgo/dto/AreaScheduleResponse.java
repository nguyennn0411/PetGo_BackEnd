package com.example.petgo.dto;

import lombok.Builder;

import java.time.LocalTime;

@Builder
public record AreaScheduleResponse(
        Long id,
        Long areaId,
        Integer dayOfWeek,
        LocalTime openTime,
        LocalTime closeTime,
        Boolean active) {
}
