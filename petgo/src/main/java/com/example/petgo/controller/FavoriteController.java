package com.example.petgo.controller;

import com.example.petgo.dto.FavoriteListResponse;
import com.example.petgo.dto.FavoriteMutationResponse;
import com.example.petgo.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{ownerUserId}/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<FavoriteListResponse> getFavorites(
            @PathVariable Long ownerUserId,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude
    ) {
        return ResponseEntity.ok(favoriteService.getFavorites(ownerUserId, latitude, longitude));
    }

    @GetMapping("/provider-ids")
    public ResponseEntity<List<Long>> getFavoriteProviderIds(@PathVariable Long ownerUserId) {
        return ResponseEntity.ok(favoriteService.getFavoriteProviderIds(ownerUserId));
    }

    @PostMapping("/providers/{providerId}")
    public ResponseEntity<FavoriteMutationResponse> addFavorite(
            @PathVariable Long ownerUserId,
            @PathVariable Long providerId
    ) {
        return ResponseEntity.ok(favoriteService.addFavorite(ownerUserId, providerId));
    }

    @DeleteMapping("/providers/{providerId}")
    public ResponseEntity<FavoriteMutationResponse> removeFavorite(
            @PathVariable Long ownerUserId,
            @PathVariable Long providerId
    ) {
        return ResponseEntity.ok(favoriteService.removeFavorite(ownerUserId, providerId));
    }
}
