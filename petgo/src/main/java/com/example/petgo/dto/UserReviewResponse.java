package com.example.petgo.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UserReviewResponse(
        Long reviewId,
        Long bookingId,
        Long providerId,
        String providerName,
        String providerImage,
        String serviceName,
        String petName,
        Integer rating,
        String comment,
        String status,
        String createdAt,
        List<ReviewPhotoResponse> photos
) {
}
