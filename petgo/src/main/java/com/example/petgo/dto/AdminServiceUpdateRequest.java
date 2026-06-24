package com.example.petgo.dto;

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
public class AdminServiceUpdateRequest {

    String name;
    String shortDescription;
    String description;
    Integer defaultDurationMinutes;
    BigDecimal basePriceAmount;
    String imageUrl;
    Boolean active;
    String bookingType;
    List<Long> categoryIds;
}
