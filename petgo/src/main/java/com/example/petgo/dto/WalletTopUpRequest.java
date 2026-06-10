package com.example.petgo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WalletTopUpRequest(
                @NotNull(message = "Số tiền nạp không được để trống") @DecimalMin(value = "50000.00", message = "Số tiền nạp tối thiểu là 50.000") BigDecimal amount,
                String returnUrl,
                String cancelUrl,
                String note) {
}