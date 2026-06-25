package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ShippingFeeResponse(
        BigDecimal distanceKm,
        BigDecimal shippingFee,
        Long feeConfigId,
        String message) {
}
