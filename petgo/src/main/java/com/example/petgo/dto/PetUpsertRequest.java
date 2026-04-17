package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PetUpsertRequest(
        @NotBlank(message = "Tên thú cưng không được để trống")
        @Size(max = 120, message = "Tên thú cưng tối đa 120 ký tự")
        String name,

        @NotBlank(message = "Loại thú cưng không được để trống")
        String species,

        @Size(max = 120, message = "Giống thú cưng tối đa 120 ký tự")
        String breed,

        String gender,
        LocalDate dateOfBirth,

        @Size(max = 50, message = "Nhãn độ tuổi tối đa 50 ký tự")
        String ageLabel,

        BigDecimal weightKg,

        @Size(max = 100, message = "Màu sắc tối đa 100 ký tự")
        String color,

        String size,

        @Size(max = 500, message = "Avatar URL tối đa 500 ký tự")
        String avatarUrl,

        String healthNotes,
        String allergyNotes,
        String behaviorNotes,
        String vaccinationNotes,

        List<@NotBlank(message = "URL ảnh không được để trống") String> photoUrls
) {
}
