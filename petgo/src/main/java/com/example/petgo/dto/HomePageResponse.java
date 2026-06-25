package com.example.petgo.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record HomePageResponse(
                List<HomeSliderResponse> sliders,
                List<HomeCategoryResponse> categories,
                List<HomeMembershipPlanResponse> membershipPlans,
                HomeStatsResponse stats) {
}
