package com.example.petgo.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record HomePageResponse(
                List<HomeSliderResponse> sliders,
                List<HomeCategoryResponse> categories,
                List<HomeProviderResponse> nearbyProviders,
                List<HomeProviderResponse> featuredProviders,
                List<HomeMembershipPlanResponse> membershipPlans,
                List<HomeReviewResponse> reviews,
                HomeStatsResponse stats) {
}
