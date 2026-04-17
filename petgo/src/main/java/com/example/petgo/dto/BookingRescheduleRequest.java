package com.example.petgo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookingRescheduleRequest(
        @NotNull(message = "ownerUserId là bắt buộc")
        Long ownerUserId,
        Long newSlotId,
        String newAppointmentDate,
        String newStartTime,
        @Size(max = 255, message = "Ghi chú đổi lịch tối đa 255 ký tự")
        String note
) {
}
