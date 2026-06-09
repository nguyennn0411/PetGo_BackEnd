package com.example.petgo.exception;

import java.math.BigDecimal;

public class InsufficientWalletBalanceException extends BadRequestException {

    private final BigDecimal requiredAmount;
    private final BigDecimal currentBalance;
    private final BigDecimal missingAmount;
    private final String topUpUrl;

    public InsufficientWalletBalanceException(BigDecimal requiredAmount, BigDecimal currentBalance,
            BigDecimal missingAmount, String topUpUrl) {
        super("INSUFFICIENT_WALLET_BALANCE");
        this.requiredAmount = requiredAmount;
        this.currentBalance = currentBalance;
        this.missingAmount = missingAmount;
        this.topUpUrl = topUpUrl;
    }

    public BigDecimal getRequiredAmount() {
        return requiredAmount;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public BigDecimal getMissingAmount() {
        return missingAmount;
    }

    public String getTopUpUrl() {
        return topUpUrl;
    }
}