package com.example.petgo.service;

import com.example.petgo.dto.CreateReviewRequest;
import com.example.petgo.dto.ReviewResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(HttpServletRequest request, CreateReviewRequest req);
    List<ReviewResponse> getReviewsByService(Long serviceId);
    boolean hasReviewed(HttpServletRequest request, Long bookingId);
}
