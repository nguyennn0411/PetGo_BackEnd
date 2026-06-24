package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record BookingCreateResponse(
        Long id,
        String bookingCode,
        String status,
        LocalDate appointmentDate,
        LocalTime startTime,
        LocalTime endTime,
        BigDecimal priceAmount,
        BigDecimal shippingFee,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        String message) {
}
