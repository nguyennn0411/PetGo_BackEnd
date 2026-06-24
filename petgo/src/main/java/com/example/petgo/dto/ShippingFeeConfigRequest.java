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
public class ShippingFeeConfigRequest {

    @NotNull(message = "Số km bắt đầu không được để trống")
    BigDecimal fromKm;

    BigDecimal toKm;

    @NotNull(message = "Phí vận chuyển không được để trống")
    BigDecimal fee;

    @Builder.Default
    Boolean active = true;
}
