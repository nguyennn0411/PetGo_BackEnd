package com.example.petgo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateReviewRequest(
        @NotNull Long bookingId,
        @NotNull @Min(1) @Max(5) Integer rating,
        String content) {
}
