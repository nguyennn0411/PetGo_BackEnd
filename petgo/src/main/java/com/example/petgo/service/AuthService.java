package com.example.petgo.service;

import com.example.petgo.dto.request.LoginRequest;
import com.example.petgo.dto.request.RegisterRequest;
import com.example.petgo.dto.response.AuthResponse;
import com.example.petgo.dto.response.UserResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    UserResponse register(RegisterRequest request);
}
