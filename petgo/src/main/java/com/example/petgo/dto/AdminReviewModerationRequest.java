package com.example.petgo.dto;

import jakarta.validation.constraints.Size;

public record AdminReviewModerationRequest(
        String status,
        @Size(max = 1000, message = "Ghi chú admin tối đa 1000 ký tự") String adminNote) {
}