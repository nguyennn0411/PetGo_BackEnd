package com.example.petgo.service;

import com.example.petgo.dto.FavoriteListResponse;
import com.example.petgo.dto.FavoriteMutationResponse;

import java.util.List;

public interface FavoriteService {
    FavoriteListResponse getFavorites(Long ownerUserId, Double latitude, Double longitude);
    List<Long> getFavoriteProviderIds(Long ownerUserId);
    FavoriteMutationResponse addFavorite(Long ownerUserId, Long providerId);
    FavoriteMutationResponse removeFavorite(Long ownerUserId, Long providerId);
}
