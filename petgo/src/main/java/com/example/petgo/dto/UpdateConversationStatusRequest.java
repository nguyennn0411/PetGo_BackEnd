package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateConversationStatusRequest(
        @NotBlank(message = "Trạng thái không được để trống")
        String status) {
}
