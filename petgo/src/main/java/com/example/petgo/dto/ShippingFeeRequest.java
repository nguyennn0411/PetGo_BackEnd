package com.example.petgo.dto;

import jakarta.validation.constraints.NotNull;
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
public class ShippingFeeRequest {

    @NotNull(message = "ID khu vực không được để trống")
    Long areaId;

    @NotNull(message = "Vĩ độ không được để trống")
    BigDecimal pickupLatitude;

    @NotNull(message = "Kinh độ không được để trống")
    BigDecimal pickupLongitude;
}
