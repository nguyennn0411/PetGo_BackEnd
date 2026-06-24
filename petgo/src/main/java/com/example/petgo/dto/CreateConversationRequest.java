package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateConversationRequest(
        @NotNull(message = "Loại hội thoại không được để trống")
        String type,

        @NotBlank(message = "Tiêu đề không được để trống")
        String title,

        @NotBlank(message = "Nội dung tin nhắn không được để trống")
        String content,

        String imageUrl,
        String errorCode) {
}
