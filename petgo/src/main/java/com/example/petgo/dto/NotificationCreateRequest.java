package com.example.petgo.dto;

import com.example.petgo.entity.NotificationAudienceType;
import com.example.petgo.entity.NotificationCategory;
import com.example.petgo.entity.NotificationPriority;
import com.example.petgo.entity.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record NotificationCreateRequest(
        @NotBlank(message = "Tiêu đề thông báo không được để trống.") @Size(max = 180, message = "Tiêu đề thông báo không được vượt quá 180 ký tự.") String title,

        @NotBlank(message = "Nội dung thông báo không được để trống.") @Size(max = 4000, message = "Nội dung thông báo không được vượt quá 4000 ký tự.") String content,

        NotificationAudienceType audienceType,
        List<Long> recipientUserIds,
        List<RoleType> targetRoles,
        NotificationCategory category,
        NotificationPriority priority,

        @Size(max = 500, message = "Liên kết hành động không được vượt quá 500 ký tự.") String actionUrl,

        LocalDateTime expiresAt) {
}