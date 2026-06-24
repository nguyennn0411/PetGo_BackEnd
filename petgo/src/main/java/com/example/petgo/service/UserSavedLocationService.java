package com.example.petgo.service;

import com.example.petgo.dto.UserSavedLocationRequest;
import com.example.petgo.dto.UserSavedLocationResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserSavedLocationService {
    List<UserSavedLocationResponse> getUserLocations(HttpServletRequest request);
    UserSavedLocationResponse createLocation(HttpServletRequest request, UserSavedLocationRequest req);
    UserSavedLocationResponse updateLocation(HttpServletRequest request, Long id, UserSavedLocationRequest req);
    void deleteLocation(HttpServletRequest request, Long id);
}
