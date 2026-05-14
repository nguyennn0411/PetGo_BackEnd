package com.example.petgo.dto.partner;

import lombok.Builder;

import java.util.List;

@Builder
public record PartnerScheduleResponse(
        Long providerId,
        List<PartnerBusinessHourResponse> weeklyHours,
        List<PartnerSlotResponse> slots) {
}