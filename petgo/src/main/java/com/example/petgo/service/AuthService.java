package com.example.petgo.service;

import com.example.petgo.dto.request.LoginRequest;
import com.example.petgo.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
}
