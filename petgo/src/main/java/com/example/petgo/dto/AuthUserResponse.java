package com.example.petgo.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record AuthUserResponse(
        Long id,
        Long userId,
        Long ownerUserId,
        String userCode,
        String fullName,
        String name,
        String email,
        String phoneNumber,
        String avatarUrl,
        String coverUrl,
        String addressLine1,
        String addressLine2,
        String ward,
        String district,
        String city,
        String province,
        String countryCode,
        String address,
        String status,
        String createdAt,
        List<String> roles
) {
}
