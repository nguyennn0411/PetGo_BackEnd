package com.example.petgo.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatParticipantResponse(
        Long userId,
        String fullName,
        String avatarUrl,
        String roleInChat,
        LocalDateTime joinedAt) {
}