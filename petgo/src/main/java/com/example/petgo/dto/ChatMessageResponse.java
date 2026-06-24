package com.example.petgo.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatMessageResponse(
                Long id,
                Long conversationId,
                Long senderId,
                String senderName,
                String senderAvatarUrl,
                String messageType,
                String content,
                String attachmentUrl,
                String status,
                LocalDateTime createdAt,
                Boolean canDelete) {
}