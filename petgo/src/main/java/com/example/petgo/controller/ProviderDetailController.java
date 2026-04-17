package com.example.petgo.controller;

import com.example.petgo.dto.ProviderDetailResponse;
import com.example.petgo.service.ProviderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
public class ProviderDetailController {

    private final ProviderDetailService providerDetailService;

    @GetMapping("/{providerId}")
    public ResponseEntity<ProviderDetailResponse> getProviderDetail(
            @PathVariable Long providerId,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude
    ) {
        return ResponseEntity.ok(providerDetailService.getProviderDetail(providerId, latitude, longitude));
    }
}
