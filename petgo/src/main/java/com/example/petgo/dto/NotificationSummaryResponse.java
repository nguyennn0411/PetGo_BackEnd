package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record NotificationSummaryResponse(
        long total,
        long unread,
        long read) {
}