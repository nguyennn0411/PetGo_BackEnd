package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PaymentCheckoutRequest(
        @NotNull(message = "Thiếu bookingId")
        Long bookingId,

        @NotBlank(message = "Vui lòng chọn phương thức thanh toán")
        String paymentMethod,

        @Size(max = 50, message = "Mã promo không hợp lệ")
        String promoCode
) {
}
