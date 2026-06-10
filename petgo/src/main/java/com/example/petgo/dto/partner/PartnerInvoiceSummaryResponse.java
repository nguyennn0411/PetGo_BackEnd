package com.example.petgo.dto.partner;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PartnerInvoiceSummaryResponse(
        Long invoiceId,
        String invoiceNumber,
        String invoiceStatus,
        String invoiceType,
        Long bookingId,
        String bookingCode,
        String customerName,
        String serviceName,
        String petName,
        String appointmentDate,
        String appointmentDateDisplay,
        String appointmentTime,
        String issuedAt,
        String paidAt,
        BigDecimal totalAmount,
        String totalAmountDisplay,
        String currencyCode,
        Long paymentId,
        String paymentCode,
        String paymentMethod,
        String paymentStatus) {
}