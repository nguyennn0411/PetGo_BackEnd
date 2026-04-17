package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record AuthTokenBundleResponse(
        String token,
        String refreshToken,
        AuthUserResponse user
) {
}
