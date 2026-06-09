package com.example.petgo.controller;

import com.example.petgo.dto.AdminReviewListResponse;
import com.example.petgo.dto.AdminReviewModerationRequest;
import com.example.petgo.dto.AdminReviewResponse;
import com.example.petgo.service.AdminReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {
    private final AdminReviewService adminReviewService;

    @GetMapping
    public ResponseEntity<AdminReviewListResponse> listReviews(HttpServletRequest request,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(adminReviewService.listReviews(request, status, keyword));
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<AdminReviewResponse> moderateReview(HttpServletRequest request, @PathVariable Long reviewId,
            @Valid @RequestBody AdminReviewModerationRequest requestBody) {
        return ResponseEntity.ok(adminReviewService.moderateReview(request, reviewId, requestBody));
    }
}