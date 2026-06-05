package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record WalletTransactionResponse(
                Long id,
                String transactionCode,
                Long userId,
                String userCode,
                String userName,
                Long counterpartyUserId,
                String counterpartyUserCode,
                String counterpartyUserName,
                String type,
                String status,
                BigDecimal amount,
                BigDecimal balanceBefore,
                BigDecimal balanceAfter,
                String gatewayName,
                String gatewayTransactionId,
                String checkoutUrl,
                String qrCodeText,
                String paymentContent,
                String bankName,
                String bankAccountNumber,
                String bankAccountHolder,
                String note,
                String reviewNote,
                String reviewedAt,
                String createdAt) {
}