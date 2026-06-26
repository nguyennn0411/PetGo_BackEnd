package com.example.petgo.dto;

import java.math.BigDecimal;

public record PriceTierDTO(
        Long id,
        String species,
        BigDecimal weightFrom,
        BigDecimal weightTo,
        BigDecimal priceAmount) {
}
