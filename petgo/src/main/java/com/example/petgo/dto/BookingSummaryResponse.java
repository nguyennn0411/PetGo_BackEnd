package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BookingSummaryResponse(
        Long bookingId,
        String bookingCode,
        String status,
        Long ownerUserId,
        Long petId,
        Long providerId,
        Long providerServiceId,
        Long slotId,
        String providerName,
        String providerPhone,
        String providerAddress,
        String serviceName,
        Integer serviceDurationMinutes,
        String petName,
        String petBreed,
        String appointmentDate,
        String startTime,
        String endTime,
        BigDecimal subtotalAmount,
        BigDecimal totalAmount,
        String totalAmountDisplay,
        String currencyCode,
        String customerNote,
        String createdAt
) {
}
