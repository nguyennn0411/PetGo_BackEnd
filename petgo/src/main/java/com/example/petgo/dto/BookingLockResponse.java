package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record BookingLockResponse(
        Long lockId,
        Long ownerUserId,
        Long providerId,
        Long providerServiceId,
        String appointmentDate,
        String startTime,
        String endTime,
        String expiresAtUtc,
        Integer expiresInSeconds,
        String status,
        String message) {
}