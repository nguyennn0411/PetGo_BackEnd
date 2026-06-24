package com.example.petgo.service;

import com.example.petgo.dto.AdminReviewListResponse;
import com.example.petgo.dto.AdminReviewModerationRequest;
import com.example.petgo.dto.AdminReviewResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AdminReviewService {
    AdminReviewListResponse listReviews(HttpServletRequest request, String status, String keyword);

    AdminReviewResponse moderateReview(HttpServletRequest request, Long reviewId,
            AdminReviewModerationRequest requestBody);
}