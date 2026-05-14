package com.example.petgo.dto.partner;

import lombok.Builder;

@Builder
public record PartnerBusinessHourResponse(
        Long id,
        Integer weekday,
        String weekdayLabel,
        String opensAt,
        String closesAt,
        String breakStartsAt,
        String breakEndsAt,
        Boolean closed,
        String timeLabel) {
}