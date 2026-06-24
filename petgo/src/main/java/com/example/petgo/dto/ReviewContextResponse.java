package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record ReviewContextResponse(
        Long ownerUserId,
        Long bookingId,
        String bookingCode,
        Long providerId,
        String providerName,
        String providerImage,
        String serviceName,
        String appointmentDate,
        String appointmentDateDisplay,
        boolean canReview,
        Long existingReviewId,
        String note
) {
}
