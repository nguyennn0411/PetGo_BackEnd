package com.example.petgo.service;

import com.example.petgo.dto.*;
import jakarta.servlet.http.HttpServletRequest;

public interface MembershipService {
    MembershipPlansResponse getPlans();
    MembershipSubscriptionResponse getMyMembership(HttpServletRequest request);
    MembershipCheckoutContextResponse getCheckoutContext(HttpServletRequest request, String planSlug, String promoCode);
    MembershipCheckoutResponse checkout(HttpServletRequest request, MembershipCheckoutRequest requestBody);
    MembershipSubscriptionResponse cancelAutoRenew(HttpServletRequest request, MembershipCancelRequest requestBody);
}
