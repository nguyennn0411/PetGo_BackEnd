package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record FavoriteMutationResponse(
        Long ownerUserId,
        Long providerId,
        boolean favorite,
        String message
) {
}
