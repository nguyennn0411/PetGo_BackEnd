package com.example.petgo.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record InvoiceItemResponse(
        String itemType,
        String itemName,
        String description,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
}
