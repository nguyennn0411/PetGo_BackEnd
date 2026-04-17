package com.example.petgo.config;

import java.util.List;

public record AuthenticatedUser(
        Long userId,
        String email,
        List<String> roles,
        String tokenType
) {
}
