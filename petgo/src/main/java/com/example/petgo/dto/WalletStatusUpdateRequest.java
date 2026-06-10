package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;

public record WalletStatusUpdateRequest(
        @NotBlank(message = "Trạng thái ví không được để trống") String status,
        String note) {
}