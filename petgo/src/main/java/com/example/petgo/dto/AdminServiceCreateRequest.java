package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminServiceCreateRequest {

    @NotBlank
    String serviceCode;

    @NotBlank
    String name;

    @NotEmpty
    List<Long> categoryIds;

    Integer defaultDurationMinutes;

    @NotNull
    BigDecimal basePriceAmount;

    String currencyCode;

    String priceUnit;

    String shortDescription;

    String description;

    String imageUrl;

    Boolean active;

    String bookingType;

    List<PriceTierDTO> priceTiers;
}
