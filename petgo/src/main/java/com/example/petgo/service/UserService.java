package com.example.petgo.service;

import com.example.petgo.dto.UserResponse;
import com.example.petgo.dto.UserStatusRequest;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    void updateUserStatus(UserStatusRequest request);
}
