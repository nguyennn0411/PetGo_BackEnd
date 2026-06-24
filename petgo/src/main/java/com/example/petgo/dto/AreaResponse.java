package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AreaResponse(
        Long id,
        String name,
        String wardCode,
        String districtCode,
        String provinceCode,
        BigDecimal pickupLatitude,
        BigDecimal pickupLongitude,
        String pickupAddress,
        String pickupPhone,
        String pickupInstructions,
        Integer shortSlots,
        Integer longSlots,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
