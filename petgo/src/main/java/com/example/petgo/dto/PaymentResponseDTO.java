package com.example.petgo.dto;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record PaymentResponseDTO(
        Long invoiceId,
        String invoiceNumber,
        String paymentCode,
        BigDecimal amount,
        String currencyCode,
        String status,
        String paymentMethod,

        String checkoutUrl,
        String qrImageUrl,
        String qrCodeText,
        String paymentLinkId,
        
        String createdAt,
        String expiredAt,

        Long bookingId,
        Long subscriptionId
) {
}
