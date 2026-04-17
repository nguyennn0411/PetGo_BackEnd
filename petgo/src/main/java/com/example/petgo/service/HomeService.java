package com.example.petgo.service;

import com.example.petgo.dto.HomePageResponse;

public interface HomeService {
    HomePageResponse getHomePage(Double latitude, Double longitude);
}
