package com.example.petgo.dto;

import jakarta.validation.constraints.Size;

public record AdminRegistrationReviewRequest(
        @Size(max = 4000, message = "Nội dung phản hồi tối đa 4000 ký tự")
        String message
) {
}