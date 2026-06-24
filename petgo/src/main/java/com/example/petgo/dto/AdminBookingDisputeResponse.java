package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AdminBookingDisputeResponse(
        Long bookingId,
        String bookingCode,
        String status,
        String statusLabel,
        Long customerUserId,
        String customerName,
        Long providerId,
        String providerName,
        String serviceName,
        String appointmentDate,
        String appointmentTime,
        BigDecimal escrowAmount,
        String escrowAmountDisplay,
        String disputeReason
) {
}