package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record MembershipPlanCardResponse(
        Long id,
        String planCode,
        String name,
        String slug,
        String description,
        String billingCycle,
        BigDecimal priceAmount,
        String currencyCode,
        BigDecimal discountPercent,
        BigDecimal monthlyVoucherAmount,
        Boolean priorityBooking,
        Boolean prioritySupport,
        Boolean popular,
        List<String> features,
        Boolean currentPlan
) {
}
