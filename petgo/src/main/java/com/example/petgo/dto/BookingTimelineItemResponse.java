package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record BookingTimelineItemResponse(
        String fromStatus,
        String fromStatusLabel,
        String toStatus,
        String toStatusLabel,
        String note,
        String changedBy,
        String createdAt
) {
}
