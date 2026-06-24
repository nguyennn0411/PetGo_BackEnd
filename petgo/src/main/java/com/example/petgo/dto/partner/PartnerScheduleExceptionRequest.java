package com.example.petgo.dto.partner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PartnerScheduleExceptionRequest(
        @NotNull String localDate,
        @NotBlank @Size(max = 30) String type,
        String startsAt,
        String endsAt,
        Integer maxConcurrentOverride,
        @Size(max = 1000) String reason) {
}