package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PaymentCheckoutContextResponse(
        Long bookingId,
        String bookingCode,
        String bookingStatus,
        Long ownerUserId,
        String providerName,
        String providerPhone,
        String providerAddress,
        String serviceName,
        String petName,
        String appointmentDate,
        String startTime,
        BigDecimal subtotalAmount,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        String totalAmountDisplay,
        String currencyCode,
        String promoCode,
        String promoMessage,
        Long invoiceId,
        String invoiceNumber,
        String invoiceStatus,
        String paymentStatus,
        List<String> paymentMethods
) {
}
