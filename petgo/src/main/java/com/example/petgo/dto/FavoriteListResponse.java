package com.example.petgo.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record FavoriteListResponse(
        Long ownerUserId,
        List<Long> favoriteProviderIds,
        List<FavoriteProviderResponse> items,
        Integer totalItems
) {
}
