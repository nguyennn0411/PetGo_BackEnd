package com.example.petgo.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record UserResponse(
        Long id,
        String userCode,
        String email,
        String fullName,
        String phoneNumber,
        String avatarUrl,
        String status,
        String createdAt,
        List<String> roles
) {
}