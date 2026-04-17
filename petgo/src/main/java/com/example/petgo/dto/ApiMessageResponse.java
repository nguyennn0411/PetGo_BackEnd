package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record ApiMessageResponse(
        String message
) {
}
