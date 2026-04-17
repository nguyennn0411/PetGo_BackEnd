package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MembershipCheckoutRequest(
        @NotBlank(message = "Vui lòng chọn gói membership")
        String planSlug,

        @NotBlank(message = "Vui lòng chọn phương thức thanh toán")
        String paymentMethod,

        @Size(max = 50, message = "Mã promo không hợp lệ")
        String promoCode,

        Boolean autoRenew
) {
}
