package com.example.petgo.service;

import com.example.petgo.dto.ProfileResponse;
import com.example.petgo.dto.ProfileUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface ProfileService {
    ProfileResponse getMyProfile(HttpServletRequest request);
    ProfileResponse updateMyProfile(HttpServletRequest request, ProfileUpdateRequest requestBody);
}
