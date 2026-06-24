package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record BookingSlotOptionResponse(
        Long slotId,
        Long providerServiceId,
        String serviceName,
        String date,
        String startTime,
        String endTime,
        String label,
        Integer capacityRemaining,
        Boolean selected,
        String status,
        String reason
) {
}
