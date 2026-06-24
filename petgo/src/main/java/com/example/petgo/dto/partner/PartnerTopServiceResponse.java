package com.example.petgo.dto.partner;

import lombok.Builder;

@Builder
public record PartnerTopServiceResponse(
        Long providerServiceId,
        String serviceName,
        long bookingCount) {
}