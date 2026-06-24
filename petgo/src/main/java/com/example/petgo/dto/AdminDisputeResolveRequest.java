package com.example.petgo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AdminDisputeResolveRequest(
        @NotNull(message = "Thiếu số tiền hoàn user")
        @DecimalMin(value = "0.00", message = "Số tiền hoàn user không được âm")
        BigDecimal refundToUserAmount,

        @NotNull(message = "Thiếu số tiền chuyển provider")
        @DecimalMin(value = "0.00", message = "Số tiền chuyển provider không được âm")
        BigDecimal releaseToProviderAmount,

        @Size(max = 500, message = "Lý do xử lý tối đa 500 ký tự")
        String reason
) {
}