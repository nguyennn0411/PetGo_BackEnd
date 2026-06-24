package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record ReviewCreateResponse(
        Long reviewId,
        Long bookingId,
        Long providerId,
        Integer rating,
        String status,
        String createdAt,
        String message
) {
}
