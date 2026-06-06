package com.example.petgo.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ChatConversationResponse(
        Long id,
        String type,
        String status,
        String title,
        Long providerId,
        String providerName,
        Long bookingId,
        String bookingCode,
        String lastMessagePreview,
        LocalDateTime lastMessageAt,
        LocalDateTime createdAt,
        List<ChatParticipantResponse> participants) {
}