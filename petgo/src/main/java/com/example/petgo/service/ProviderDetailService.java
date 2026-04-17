package com.example.petgo.service;

import com.example.petgo.dto.ProviderDetailResponse;

public interface ProviderDetailService {
    ProviderDetailResponse getProviderDetail(Long providerId, Double latitude, Double longitude);
}
