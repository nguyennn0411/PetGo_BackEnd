package com.example.petgo.dto.partner;

import com.example.petgo.dto.ReviewPhotoResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record PartnerReviewSummaryResponse(
        Long reviewId,
        Integer rating,
        String comment,
        String status,
        String createdAt,
        Long customerUserId,
        String customerName,
        String customerAvatarUrl,
        Long bookingId,
        String bookingCode,
        String bookingStatus,
        String bookingStatusLabel,
        Long providerServiceId,
        String serviceName,
        Long petId,
        String petName,
        String petBreed,
        String appointmentDate,
        String appointmentDateDisplay,
        List<ReviewPhotoResponse> photos) {
}