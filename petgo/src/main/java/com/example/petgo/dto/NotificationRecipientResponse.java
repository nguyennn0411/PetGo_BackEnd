package com.example.petgo.dto;

import com.example.petgo.entity.NotificationCategory;
import com.example.petgo.entity.NotificationPriority;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationRecipientResponse(
        Long id,
        Long notificationId,
        String title,
        String content,
        NotificationCategory category,
        NotificationPriority priority,
        String actionUrl,
        LocalDateTime sentAt,
        LocalDateTime expiresAt,
        LocalDateTime deliveredAt,
        LocalDateTime readAt,
        boolean read,
        String createdByName) {
}