package com.example.petgo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WalletWithdrawRequest(
        @NotNull(message = "Số tiền rút không được để trống") @DecimalMin(value = "50000.00", message = "Số tiền rút tối thiểu là 50.000") BigDecimal amount,
        @NotBlank(message = "Ngân hàng thụ hưởng không được để trống") String bankName,
        @NotBlank(message = "Số tài khoản thụ hưởng không được để trống") String bankAccountNumber,
        @NotBlank(message = "Tên chủ tài khoản thụ hưởng không được để trống") String bankAccountHolder,
        String note) {
}