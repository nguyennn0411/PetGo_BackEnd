package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookingDisputeRequest(
        @NotBlank(message = "Vui lòng nhập lý do khiếu nại")
        @Size(min = 10, max = 500, message = "Lý do khiếu nại cần từ 10 đến 500 ký tự")
        String reason
) {
}