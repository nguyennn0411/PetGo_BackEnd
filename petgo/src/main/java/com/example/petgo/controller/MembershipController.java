package com.example.petgo.controller;

import com.example.petgo.dto.*;
import com.example.petgo.service.MembershipService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @GetMapping("/plans")
    public ResponseEntity<MembershipPlansResponse> getPlans() {
        return ResponseEntity.ok(membershipService.getPlans());
    }

    @GetMapping("/me")
    public ResponseEntity<MembershipSubscriptionResponse> getMyMembership(HttpServletRequest request) {
        return ResponseEntity.ok(membershipService.getMyMembership(request));
    }

    @GetMapping("/checkout-context")
    public ResponseEntity<MembershipCheckoutContextResponse> getCheckoutContext(HttpServletRequest request,
                                                                                @RequestParam String planSlug,
                                                                                @RequestParam(required = false) String promoCode) {
        return ResponseEntity.ok(membershipService.getCheckoutContext(request, planSlug, promoCode));
    }

    @PostMapping("/checkout")
    public ResponseEntity<MembershipCheckoutResponse> checkout(HttpServletRequest request,
                                                               @Valid @RequestBody MembershipCheckoutRequest requestBody) {
        return ResponseEntity.status(HttpStatus.CREATED).body(membershipService.checkout(request, requestBody));
    }

    @PostMapping("/me/cancel")
    public ResponseEntity<MembershipSubscriptionResponse> cancelAutoRenew(HttpServletRequest request,
                                                                          @RequestBody(required = false) MembershipCancelRequest requestBody) {
        return ResponseEntity.ok(membershipService.cancelAutoRenew(request, requestBody));
    }
}
