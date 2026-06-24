package com.example.petgo.dto.partner;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PartnerRevenueByServiceResponse(
        Long providerServiceId,
        String serviceName,
        long bookingCount,
        BigDecimal revenue,
        String revenueDisplay) {
}