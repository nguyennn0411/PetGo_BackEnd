package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BookingListItemResponse(
        Long bookingId,
        String bookingCode,
        String status,
        String statusLabel,
        String providerName,
        String providerImage,
        String serviceName,
        String petLabel,
        String appointmentDate,
        String appointmentDateDisplay,
        String appointmentTime,
        String totalAmountDisplay,
        BigDecimal totalAmount,
        String currencyCode,
        Long invoiceId,
        boolean canCancel,
        boolean canReschedule,
        boolean canReview
) {
}
