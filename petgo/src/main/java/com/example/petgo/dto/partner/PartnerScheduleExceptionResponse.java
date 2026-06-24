package com.example.petgo.dto.partner;

import lombok.Builder;

@Builder
public record PartnerScheduleExceptionResponse(
        Long id,
        String localDate,
        String type,
        String startsAt,
        String endsAt,
        Integer maxConcurrentOverride,
        String reason) {
}