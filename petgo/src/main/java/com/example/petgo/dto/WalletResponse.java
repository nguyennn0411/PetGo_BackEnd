package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record WalletResponse(Long walletId, Long userId, String userCode, String fullName, BigDecimal balance,
        BigDecimal heldBalance, String currencyCode, String status, Boolean isSystem) {
}