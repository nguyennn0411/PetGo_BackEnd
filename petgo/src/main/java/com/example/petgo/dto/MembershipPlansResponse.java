package com.example.petgo.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record MembershipPlansResponse(
        List<MembershipPlanCardResponse> plans,
        MembershipSubscriptionResponse currentSubscription
) {
}
