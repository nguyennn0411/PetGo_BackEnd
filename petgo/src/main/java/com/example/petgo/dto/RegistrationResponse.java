package com.example.petgo.dto;

import com.example.petgo.entity.RegistrationStatus;
import com.example.petgo.entity.RegistrationType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record RegistrationResponse(
        Long id,
        RegistrationType type,
        RegistrationStatus status,
        Long userId,
        String userName,
        String userEmail,
        String userPhone,
        String businessName,
        String businessPhone,
        String businessEmail,
        String businessAddress,
        String taxCode,
        String representativeName,
        String representativePhone,
        String representativeEmail,
        String description,
        List<Long> serviceCategoryIds,
        List<RegistrationServiceCategoryResponse> serviceCategories,
        List<String> locationImageUrls,
        String additionalInformation,
        String adminMessage,
        String rejectionReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime submittedAt,
        LocalDateTime reviewedAt,
        Long reviewerId,
        String reviewerName
) {
}