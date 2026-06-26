package com.example.petgo.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReviewResponse(
        Long id,
        Long bookingId,
        Long userId,
        String userName,
        String userAvatar,
        Long serviceId,
        String serviceName,
        Integer rating,
        String content,
        Boolean hidden,
        String reply,
        LocalDateTime createdAt) {
}
