package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record ProviderVerificationRequest(
    Long providerId,
    String status
) {}
