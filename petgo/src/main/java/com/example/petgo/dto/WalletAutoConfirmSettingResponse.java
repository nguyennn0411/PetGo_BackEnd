package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record WalletAutoConfirmSettingResponse(Boolean enabled) {
}