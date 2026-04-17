package com.example.petgo.dto;

import jakarta.validation.constraints.Size;

public record MembershipCancelRequest(
        @Size(max = 255, message = "Lý do tối đa 255 ký tự")
        String reason
) {
}
