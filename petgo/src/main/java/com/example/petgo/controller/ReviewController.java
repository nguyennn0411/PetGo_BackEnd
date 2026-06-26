package com.example.petgo.controller;

import com.example.petgo.dto.CreateReviewRequest;
import com.example.petgo.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createReview(
            HttpServletRequest request,
            @Valid @RequestBody CreateReviewRequest req) {
        return ResponseEntity.ok(Map.of(
                "message", "Đánh giá thành công.",
                "result", reviewService.createReview(request, req)));
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<Map<String, Object>> getReviewsByService(
            @PathVariable Long serviceId) {
        return ResponseEntity.ok(Map.of(
                "result", reviewService.getReviewsByService(serviceId)));
    }

    @GetMapping("/check/{bookingId}")
    public ResponseEntity<Map<String, Object>> hasReviewed(
            HttpServletRequest request,
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(Map.of(
                "result", Map.of("reviewed", reviewService.hasReviewed(request, bookingId))));
    }
}
