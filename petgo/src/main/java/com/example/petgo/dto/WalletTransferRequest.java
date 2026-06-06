package com.example.petgo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WalletTransferRequest(
                Long recipientUserId,
                String recipientAccount,
                @NotNull(message = "Số tiền chuyển không được để trống") @DecimalMin(value = "1000.00", message = "Số tiền chuyển phải lớn hơn hoặc bằng 1.000") BigDecimal amount,
                String note) {
}