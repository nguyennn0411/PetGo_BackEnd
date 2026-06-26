package com.example.petgo.service;

import com.example.petgo.dto.ReviewResponse;

import java.util.List;

public interface AdminReviewService {
    List<ReviewResponse> getAllReviews(String search, Integer rating, Boolean hidden);
    ReviewResponse getReviewDetail(Long id);
    ReviewResponse toggleHidden(Long id);
    ReviewResponse reply(Long id, String reply);
    void deleteReview(Long id);
}
