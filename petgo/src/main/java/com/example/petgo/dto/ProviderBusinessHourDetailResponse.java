package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record ProviderBusinessHourDetailResponse(
        Integer weekday,
        String days,
        String time,
        String opensAt,
        String closesAt,
        String breakStartsAt,
        String breakEndsAt,
        Boolean closed
) {
}
