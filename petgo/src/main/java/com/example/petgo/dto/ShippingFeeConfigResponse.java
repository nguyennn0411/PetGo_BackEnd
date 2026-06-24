package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ShippingFeeConfigResponse(
        Long id,
        Long areaId,
        BigDecimal fromKm,
        BigDecimal toKm,
        BigDecimal fee,
        Boolean active) {
}
