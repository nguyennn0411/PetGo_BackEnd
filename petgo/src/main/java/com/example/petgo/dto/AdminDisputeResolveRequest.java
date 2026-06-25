package com.example.petgo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AdminDisputeResolveRequest(
        @NotNull(message = "Số tiền hoàn trả khách hàng không được để trống")
        @DecimalMin(value = "0.0", message = "Số tiền hoàn trả không được âm")
        BigDecimal refundToUserAmount,

        @NotNull(message = "Số tiền giải ngân đối tác không được để trống")
        @DecimalMin(value = "0.0", message = "Số tiền giải ngân không được âm")
        BigDecimal releaseToPartnerAmount,

        @NotBlank(message = "Lý do xử lý khiếu nại không được để trống")
        String reason) {
}
