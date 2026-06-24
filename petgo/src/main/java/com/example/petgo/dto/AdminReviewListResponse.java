package com.example.petgo.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record AdminReviewListResponse(
        long totalItems,
        List<AdminReviewResponse> reviews) {
}