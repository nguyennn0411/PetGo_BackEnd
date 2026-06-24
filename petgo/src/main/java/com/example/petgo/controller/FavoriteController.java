package com.example.petgo.controller;

import com.example.petgo.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/toggle/{serviceId}")
    public ResponseEntity<Map<String, Object>> toggleFavorite(HttpServletRequest request,
                                                              @PathVariable Long serviceId) {
        boolean favorited = favoriteService.toggleFavorite(request, serviceId);
        return ResponseEntity.ok(Map.of(
                "message", favorited ? "Đã thêm vào yêu thích." : "Đã bỏ yêu thích.",
                "result", Map.of("favorited", favorited)));
    }

    @GetMapping
    public ResponseEntity<?> getFavorites(HttpServletRequest request) {
        return ResponseEntity.ok(favoriteService.getUserFavorites(request));
    }

    @GetMapping("/ids")
    public ResponseEntity<Set<Long>> getFavoriteIds(HttpServletRequest request) {
        return ResponseEntity.ok(favoriteService.getUserFavoriteIds(request));
    }
}
