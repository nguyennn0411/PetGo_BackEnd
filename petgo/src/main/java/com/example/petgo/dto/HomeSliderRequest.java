package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HomeSliderRequest(
        @NotBlank(message = "Tiêu đề slider không được để trống") @Size(max = 160, message = "Tiêu đề slider tối đa 160 ký tự") String title,

        @Size(max = 500, message = "Mô tả slider tối đa 500 ký tự") String subtitle,

        @NotBlank(message = "Ảnh slider không được để trống") @Size(max = 1000, message = "URL ảnh slider tối đa 1000 ký tự") String imageUrl,

        @Size(max = 80, message = "Nhãn nút tối đa 80 ký tự") String ctaLabel,

        @Size(max = 500, message = "URL điều hướng tối đa 500 ký tự") String ctaUrl,

        Integer sortOrder,
        Boolean active) {
}