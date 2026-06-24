package com.example.petgo.dto.partner;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PartnerWeeklyScheduleRequest(
        @NotEmpty(message = "Vui lòng cấu hình ít nhất một ngày") List<@Valid PartnerBusinessHourRequest> weeklyHours) {
}