package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record InvoiceDetailResponse(
        Long invoiceId,
        String invoiceNumber,
        String invoiceStatus,
        String invoiceType,
        String issuedAt,
        String paidAt,
        Long paymentId,
        String paymentCode,
        String paymentMethod,
        String paymentStatus,
        BigDecimal subtotalAmount,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        String currencyCode,
        String totalAmountDisplay,
        List<InvoiceItemResponse> items
) {
}
