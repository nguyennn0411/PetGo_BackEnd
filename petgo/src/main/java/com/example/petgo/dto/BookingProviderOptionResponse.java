package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BookingProviderOptionResponse(
        Long id,
        String name,
        String headline,
        String address,
        BigDecimal rating,
        Boolean instantBooking,
        String image
) {
}
