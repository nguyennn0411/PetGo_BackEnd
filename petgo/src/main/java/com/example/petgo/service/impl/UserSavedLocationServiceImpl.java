package com.example.petgo.service.impl;

import com.example.petgo.dto.UserSavedLocationRequest;
import com.example.petgo.dto.UserSavedLocationResponse;
import com.example.petgo.entity.UserSavedLocation;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.UserSavedLocationRepository;
import com.example.petgo.service.AuthService;
import com.example.petgo.service.UserSavedLocationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSavedLocationServiceImpl implements UserSavedLocationService {

    private final UserSavedLocationRepository repository;
    private final AuthService authService;

    @Override
    @Transactional(readOnly = true)
    public List<UserSavedLocationResponse> getUserLocations(HttpServletRequest request) {
        Long userId = authService.requireAccessUser(request).userId();
        return repository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public UserSavedLocationResponse createLocation(HttpServletRequest request, UserSavedLocationRequest req) {
        Long userId = authService.requireAccessUser(request).userId();
        UserSavedLocation loc = new UserSavedLocation();
        loc.setUserId(userId);
        loc.setName(req.getName());
        loc.setLatitude(req.getLatitude());
        loc.setLongitude(req.getLongitude());
        loc.setAddress(req.getAddress());
        return toResponse(repository.save(loc));
    }

    @Override
    @Transactional
    public UserSavedLocationResponse updateLocation(HttpServletRequest request, Long id, UserSavedLocationRequest req) {
        Long userId = authService.requireAccessUser(request).userId();
        UserSavedLocation loc = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Địa điểm không tồn tại."));
        if (!loc.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Địa điểm không tồn tại.");
        }
        loc.setName(req.getName());
        loc.setLatitude(req.getLatitude());
        loc.setLongitude(req.getLongitude());
        loc.setAddress(req.getAddress());
        return toResponse(repository.save(loc));
    }

    @Override
    @Transactional
    public void deleteLocation(HttpServletRequest request, Long id) {
        Long userId = authService.requireAccessUser(request).userId();
        UserSavedLocation loc = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Địa điểm không tồn tại."));
        if (!loc.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Địa điểm không tồn tại.");
        }
        repository.delete(loc);
    }

    private UserSavedLocationResponse toResponse(UserSavedLocation loc) {
        return UserSavedLocationResponse.builder()
                .id(loc.getId())
                .name(loc.getName())
                .latitude(loc.getLatitude())
                .longitude(loc.getLongitude())
                .address(loc.getAddress())
                .createdAt(loc.getCreatedAt())
                .updatedAt(loc.getUpdatedAt())
                .build();
    }
}
