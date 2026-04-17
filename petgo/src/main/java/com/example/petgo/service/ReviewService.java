package com.example.petgo.service;

import com.example.petgo.dto.ReviewContextResponse;
import com.example.petgo.dto.ReviewCreateRequest;
import com.example.petgo.dto.ReviewCreateResponse;
import com.example.petgo.dto.UserReviewListResponse;

public interface ReviewService {
    ReviewContextResponse getReviewContext(Long ownerUserId, Long bookingId);
    ReviewCreateResponse createReview(Long ownerUserId, Long bookingId, ReviewCreateRequest request);
    UserReviewListResponse getMyReviews(Long ownerUserId);
}
