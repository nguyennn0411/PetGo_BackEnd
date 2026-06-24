package com.example.petgo.dto.partner;

import com.example.petgo.dto.InvoiceItemResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PartnerInvoiceDetailResponse(
        Long invoiceId,
        String invoiceNumber,
        String invoiceStatus,
        String invoiceType,
        String issuedAt,
        String paidAt,
        Long bookingId,
        String bookingCode,
        Long customerUserId,
        String customerName,
        String customerPhone,
        String customerEmail,
        Long providerServiceId,
        String serviceName,
        String petName,
        String appointmentDate,
        String appointmentDateDisplay,
        String appointmentTime,
        BigDecimal subtotalAmount,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        String totalAmountDisplay,
        String currencyCode,
        Long paymentId,
        String paymentCode,
        String paymentMethod,
        String paymentStatus,
        List<InvoiceItemResponse> items) {
}