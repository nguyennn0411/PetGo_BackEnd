package com.example.petgo.dto.partner;

import lombok.Builder;

@Builder
public record PartnerSlotResponse(
        Long id,
        Long providerServiceId,
        String serviceName,
        String date,
        String dateDisplay,
        String startTime,
        String endTime,
        String timeLabel,
        String slotStatus,
        Integer capacityTotal,
        Integer capacityBooked,
        Integer capacityRemaining,
        String note) {
}