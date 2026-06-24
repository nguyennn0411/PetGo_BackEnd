package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AreaRequest {

    @NotBlank(message = "Tên khu vực không được để trống")
    String name;

    String wardCode;
    String districtCode;
    String provinceCode;

    BigDecimal pickupLatitude;
    BigDecimal pickupLongitude;
    String pickupAddress;
    String pickupPhone;
    String pickupInstructions;

    @Builder.Default
    Integer shortSlots = 10;

    @Builder.Default
    Integer longSlots = 3;
}
