package com.example.petgo.dto;

import java.time.LocalDateTime;

public record ConversationResponse(
        Long id,
        Long userId,
        String userName,
        String type,
        String typeLabel,
        String status,
        String statusLabel,
        String title,
        int messageCount,
        MessageResponse lastMessage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
