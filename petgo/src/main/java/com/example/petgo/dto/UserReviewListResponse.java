package com.example.petgo.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UserReviewListResponse(
        Long ownerUserId,
        List<UserReviewResponse> reviews,
        Integer totalItems
) {
}
