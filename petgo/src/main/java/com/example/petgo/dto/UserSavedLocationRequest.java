package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSavedLocationRequest {

    @NotBlank(message = "Tên địa điểm không được để trống")
    String name;

    @NotNull(message = "Vĩ độ không được để trống")
    BigDecimal latitude;

    @NotNull(message = "Kinh độ không được để trống")
    BigDecimal longitude;

    String address;
}
