package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;

public record WalletAdminReviewRequest(
        @NotBlank(message = "Trạng thái duyệt không được để trống") String action,
        String reviewNote) {
}