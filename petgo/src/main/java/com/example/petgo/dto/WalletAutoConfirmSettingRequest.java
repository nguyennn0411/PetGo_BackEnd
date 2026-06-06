package com.example.petgo.dto;

import jakarta.validation.constraints.NotNull;

public record WalletAutoConfirmSettingRequest(
        @NotNull(message = "Trạng thái tự động cộng tiền không được để trống") Boolean enabled) {
}