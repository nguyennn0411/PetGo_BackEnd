package com.example.petgo.dto;

import java.math.BigDecimal;

public record AdminBookingDisputeResponse(
        Long bookingId,
        String bookingCode,
        Long customerUserId,
        String customerName,
        Long areaId,
        String areaName,
        String serviceName,
        String appointmentDate,
        String appointmentTime,
        BigDecimal escrowAmount,
        String escrowAmountDisplay,
        String disputeReason,
        String status,
        String statusLabel) {
}
