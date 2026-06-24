package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record ProviderSlotResponse(
        Long id,
        Long providerServiceId,
        String serviceName,
        String date,
        String startTime,
        String endTime,
        String label,
        Integer capacityRemaining
) {
}
