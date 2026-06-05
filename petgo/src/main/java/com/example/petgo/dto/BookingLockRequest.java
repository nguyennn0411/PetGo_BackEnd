package com.example.petgo.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record BookingLockRequest(
        @NotNull(message = "Thiếu ownerUserId") Long ownerUserId,

        @NotNull(message = "Thiếu providerId") Long providerId,

        @NotNull(message = "Vui lòng chọn dịch vụ") Long providerServiceId,

        @NotNull(message = "Vui lòng chọn ngày hẹn") LocalDate appointmentDate,

        @NotNull(message = "Vui lòng chọn giờ hẹn") LocalTime startTime,

        Integer durationMinutes) {
}