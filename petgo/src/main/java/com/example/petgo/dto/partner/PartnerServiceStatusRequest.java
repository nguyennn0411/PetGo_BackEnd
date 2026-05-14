package com.example.petgo.dto.partner;

import jakarta.validation.constraints.NotNull;

public record PartnerServiceStatusRequest(
        @NotNull(message = "Trạng thái active là bắt buộc") Boolean active) {
}