package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record BookingResponse(
        Long id,
        String bookingCode,
        Long userId,
        String userName,
        String userPhone,
        Long petId,
        String petName,
        Long areaId,
        String areaName,
        Long serviceId,
        String serviceName,
        LocalDate appointmentDate,
        LocalTime startTime,
        LocalTime endTime,
        String timeSlot,
        String bookingType,
        BigDecimal shippingFee,
        BigDecimal priceAmount,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        String promoCode,
        String status,
        String customerNote,
        String adminNote,
        String createdAt,
        String updatedAt) {
}
