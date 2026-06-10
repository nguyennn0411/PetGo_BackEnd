package com.example.petgo.controller.partner;

import com.example.petgo.dto.partner.PartnerProfileResponse;
import com.example.petgo.dto.partner.PartnerProfileUpdateRequest;
import com.example.petgo.service.partner.PartnerProfileService;
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
@RequestMapping("/api/v1/partner/profile")
@RequiredArgsConstructor
public class PartnerProfileController {

    private final PartnerProfileService partnerProfileService;

    @GetMapping
    public ResponseEntity<PartnerProfileResponse> getProfile(HttpServletRequest request) {
        return ResponseEntity.ok(partnerProfileService.getProfile(request));
    }

    @PutMapping
    public ResponseEntity<PartnerProfileResponse> updateProfile(HttpServletRequest request,
            @Valid @RequestBody PartnerProfileUpdateRequest requestBody) {
        return ResponseEntity.ok(partnerProfileService.updateProfile(request, requestBody));
    }
}