package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record UserStatusRequest(
    Long userId,
    String status
) {}
