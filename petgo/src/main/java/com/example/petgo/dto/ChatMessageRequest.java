package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatMessageRequest(
                @NotBlank(message = "Nội dung tin nhắn không được để trống.") @Size(max = 4000, message = "Nội dung tin nhắn tối đa 4000 ký tự.") String content,
                @Size(max = 30, message = "Loại tin nhắn không hợp lệ.") String messageType,
                @Size(max = 500, message = "URL ảnh tối đa 500 ký tự.") String attachmentUrl) {
}