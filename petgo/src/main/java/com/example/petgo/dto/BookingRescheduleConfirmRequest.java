package com.example.petgo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookingRescheduleConfirmRequest(
        @NotNull(message = "ownerUserId là bắt buộc") Long ownerUserId,
        @NotNull(message = "lockId là bắt buộc") Long lockId,
        @Size(max = 255, message = "Ghi chú đổi lịch tối đa 255 ký tự") String note) {
}