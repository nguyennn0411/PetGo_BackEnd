package com.example.petgo.dto.partner;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PartnerBookingSummaryResponse(
        Long bookingId,
        String bookingCode,
        String status,
        String statusLabel,
        Long customerUserId,
        String customerName,
        String customerPhone,
        String serviceName,
        Long providerServiceId,
        Long petId,
        String petName,
        String petBreed,
        String appointmentDate,
        String appointmentDateDisplay,
        String appointmentTime,
        BigDecimal totalAmount,
        String totalAmountDisplay,
        String currencyCode,
        String paymentStatus,
        String customerNote,
        boolean canConfirm,
        boolean canStart,
        boolean canComplete,
        boolean canCancel) {
}