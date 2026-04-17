package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record ProfileResponse(
        AuthUserResponse user,
        long totalPets,
        long totalBookings,
        long totalReviews
) {
}
