package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record WalletResponse(Long walletId, Long userId, String userCode, String fullName, BigDecimal balance,
        String currencyCode, String status, Boolean isSystem) {
}