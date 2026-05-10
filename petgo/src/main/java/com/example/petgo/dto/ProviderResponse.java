package com.example.petgo.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record ProviderResponse(
    Long id,
    String providerCode,
    String businessName,
    String ownerName,
    String email,
    String phoneNumber,
    String address,
    String verificationStatus,
    String status, 
    LocalDateTime createdAt
) {}
