package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record UserSavedLocationResponse(
        Long id,
        String name,
        BigDecimal latitude,
        BigDecimal longitude,
        String address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
