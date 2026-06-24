package com.example.petgo.dto;

import com.example.petgo.entity.RegistrationStatus;
import com.example.petgo.entity.RegistrationType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AdminRegistrationSummaryResponse(
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
        LocalDateTime submittedAt,
        LocalDateTime reviewedAt,
        String adminMessage,
        String rejectionReason
) {
}