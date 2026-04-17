package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentCheckoutResponse(
        Long bookingId,
        String bookingCode,
        String bookingStatus,
        Long invoiceId,
        String invoiceNumber,
        String invoiceStatus,
        Long paymentId,
        String paymentCode,
        String paymentStatus,
        String paymentMethod,
        BigDecimal subtotalAmount,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        String totalAmountDisplay,
        String currencyCode,
        String promoCode,
        String paidAt
) {
}
