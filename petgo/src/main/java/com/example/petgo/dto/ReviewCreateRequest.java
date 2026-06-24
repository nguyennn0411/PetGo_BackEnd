package com.example.petgo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReviewCreateRequest(
        @NotNull(message = "ownerUserId là bắt buộc")
        Long ownerUserId,
        @NotNull(message = "Vui lòng chọn số sao đánh giá")
        @Min(value = 1, message = "Số sao phải từ 1 đến 5")
        @Max(value = 5, message = "Số sao phải từ 1 đến 5")
        Integer rating,
        String comment,
        List<String> photoUrls
) {
}
