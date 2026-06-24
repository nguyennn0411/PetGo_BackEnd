package com.example.petgo.controller;

import com.example.petgo.dto.ReviewContextResponse;
import com.example.petgo.dto.ReviewCreateRequest;
import com.example.petgo.dto.ReviewCreateResponse;
import com.example.petgo.dto.UserReviewListResponse;
import com.example.petgo.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{ownerUserId}")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/bookings/{bookingId}/review-context")
    public ResponseEntity<ReviewContextResponse> getReviewContext(
            @PathVariable Long ownerUserId,
            @PathVariable Long bookingId
    ) {
        return ResponseEntity.ok(reviewService.getReviewContext(ownerUserId, bookingId));
    }

    @PostMapping("/bookings/{bookingId}/reviews")
    public ResponseEntity<ReviewCreateResponse> createReview(
            @PathVariable Long ownerUserId,
            @PathVariable Long bookingId,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        return ResponseEntity.ok(reviewService.createReview(ownerUserId, bookingId, request));
    }

    @GetMapping("/reviews")
    public ResponseEntity<UserReviewListResponse> getMyReviews(@PathVariable Long ownerUserId) {
        return ResponseEntity.ok(reviewService.getMyReviews(ownerUserId));
    }
}
