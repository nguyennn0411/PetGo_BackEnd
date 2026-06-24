package com.example.petgo.dto.partner;

import java.util.List;

public record AdminPartnerServiceReviewRequest(
        String message,
        List<Long> categoryIds,
        Long categoryId) {
}