package com.example.petgo.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record AdminReviewResponse(
        Long reviewId,
        Integer rating,
        String comment,
        String status,
        String createdAt,
        Long customerUserId,
        String customerName,
        Long providerId,
        String providerName,
        Long bookingId,
        String bookingCode,
        String serviceName,
        String providerReply,
        String providerRepliedAt,
        String adminNote,
        String adminReviewedAt,
        List<ReviewPhotoResponse> photos) {
}