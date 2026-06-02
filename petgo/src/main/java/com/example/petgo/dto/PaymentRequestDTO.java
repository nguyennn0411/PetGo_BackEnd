package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PaymentRequestDTO(
        Long invoiceId,
        Long bookingId,
        Long subscriptionId,

        @NotBlank(message = "Phương thức thanh toán không được để trống")
        @Pattern(regexp = "PAYOS", message = "Phương thức thanh toán phải là PAYOS")
        String paymentMethod,
        
        @NotBlank(message = "URL nhận kết quả thành công không được để trống")
        String returnUrl,
        
        @NotBlank(message = "URL nhận kết quả hủy không được để trống")
        String cancelUrl
) {
}
