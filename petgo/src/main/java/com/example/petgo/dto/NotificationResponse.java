package com.example.petgo.dto;

import com.example.petgo.entity.NotificationAudienceType;
import com.example.petgo.entity.NotificationCategory;
import com.example.petgo.entity.NotificationPriority;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record NotificationResponse(
        Long id,
        String title,
        String content,
        NotificationCategory category,
        NotificationPriority priority,
        NotificationAudienceType audienceType,
        List<String> targetRoles,
        String actionUrl,
        LocalDateTime sentAt,
        LocalDateTime expiresAt,
        Long createdById,
        String createdByName,
        long totalRecipients,
        long readRecipients,
        long unreadRecipients) {
}