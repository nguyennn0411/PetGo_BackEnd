package com.example.petgo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record BookingCreateRequest(
        @NotNull(message = "Thiếu ownerUserId")
        Long ownerUserId,

        @NotNull(message = "Vui lòng chọn thú cưng")
        Long petId,

        @NotNull(message = "Thiếu providerId")
        Long providerId,

        @NotNull(message = "Vui lòng chọn dịch vụ")
        Long providerServiceId,

        Long slotId,

        @NotNull(message = "Vui lòng chọn ngày hẹn")
        LocalDate appointmentDate,

        @NotNull(message = "Vui lòng chọn giờ hẹn")
        LocalTime startTime,

        LocalTime endTime,

        @Size(max = 1000, message = "Ghi chú không được vượt quá 1000 ký tự")
        String customerNote
) {
}
