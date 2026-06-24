package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BookingMutationResponse(
        Long bookingId,
        String bookingCode,
        String status,
        String statusLabel,
        String appointmentDate,
        String appointmentDateDisplay,
        String appointmentTime,
        Long slotId,
        String message,
        BigDecimal refundAmount,
        String refundAmountDisplay,
        String refundStatus
) {
}
