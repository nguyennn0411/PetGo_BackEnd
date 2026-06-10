package com.example.petgo.service;

import com.example.petgo.config.AuthenticatedUser;
import com.example.petgo.dto.AuthLoginRequest;
import com.example.petgo.dto.AuthRegisterRequest;
import com.example.petgo.dto.AuthTokenBundleResponse;
import com.example.petgo.dto.ForgotPasswordRequest;
import com.example.petgo.dto.ResetPasswordRequest;
import com.example.petgo.dto.VerifyOtpRequest;
import com.example.petgo.dto.AuthUserResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthUserResponse register(AuthRegisterRequest request);
    void verifyOtp(VerifyOtpRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    AuthTokenBundleResponse login(AuthLoginRequest request);
    AuthTokenBundleResponse refresh(String authorizationHeader);
    AuthUserResponse getCurrentUserProfile(HttpServletRequest request);
    void logout(String authorizationHeader);
    AuthenticatedUser requireAccessUser(HttpServletRequest request);
}
