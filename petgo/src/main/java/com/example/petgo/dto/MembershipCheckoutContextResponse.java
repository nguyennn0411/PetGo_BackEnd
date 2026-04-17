package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record MembershipCheckoutContextResponse(
        MembershipPlanCardResponse plan,
        MembershipSubscriptionResponse currentSubscription,
        BigDecimal subtotalAmount,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        String totalAmountDisplay,
        String currencyCode,
        String promoCode,
        String promoMessage,
        Boolean autoRenewDefault,
        List<String> paymentMethods
) {
}
