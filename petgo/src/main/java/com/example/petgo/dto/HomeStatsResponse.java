package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record HomeStatsResponse(
        long activeCategories,
        long activeMembershipPlans
) {
}
