package com.example.petgo.dto;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        Long conversationId,
        Long senderId,
        String senderName,
        String content,
        String imageUrl,
        String errorCode,
        boolean isSystemMessage,
        LocalDateTime createdAt) {
}
