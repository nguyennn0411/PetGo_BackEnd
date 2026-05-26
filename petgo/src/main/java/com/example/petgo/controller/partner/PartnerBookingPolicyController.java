package com.example.petgo.controller.partner;

import com.example.petgo.dto.partner.PartnerBookingPolicyRequest;
import com.example.petgo.dto.partner.PartnerBookingPolicyResponse;
import com.example.petgo.service.partner.PartnerBookingPolicyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/partner/booking-policy")
@RequiredArgsConstructor
public class PartnerBookingPolicyController {

    private final PartnerBookingPolicyService partnerBookingPolicyService;

    @GetMapping
    public ResponseEntity<PartnerBookingPolicyResponse> getPolicy(HttpServletRequest request) {
        return ResponseEntity.ok(partnerBookingPolicyService.getPolicy(request));
    }

    @PutMapping
    public ResponseEntity<PartnerBookingPolicyResponse> updatePolicy(HttpServletRequest request,
            @Valid @RequestBody PartnerBookingPolicyRequest requestBody) {
        return ResponseEntity.ok(partnerBookingPolicyService.updatePolicy(request, requestBody));
    }
}