package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(
        @NotBlank(message = "Nội dung tin nhắn không được để trống")
        String content,

        String imageUrl,
        String errorCode) {
}
