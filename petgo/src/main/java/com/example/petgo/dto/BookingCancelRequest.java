package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookingCancelRequest(
        @NotNull(message = "ownerUserId là bắt buộc")
        Long ownerUserId,
        @NotBlank(message = "reasonCode là bắt buộc")
        @Size(max = 50, message = "reasonCode tối đa 50 ký tự")
        String reasonCode,
        @Size(max = 255, message = "Lý do bổ sung tối đa 255 ký tự")
        String reasonText
) {
}
