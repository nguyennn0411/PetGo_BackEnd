package com.example.petgo.dto.partner;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PartnerBusinessHourRequest(
        @NotNull(message = "weekday là bắt buộc") @Min(value = 1, message = "weekday phải từ 1 đến 7") @Max(value = 7, message = "weekday phải từ 1 đến 7") Integer weekday,
        String opensAt,
        String closesAt,
        String breakStartsAt,
        String breakEndsAt,
        Boolean closed) {
}