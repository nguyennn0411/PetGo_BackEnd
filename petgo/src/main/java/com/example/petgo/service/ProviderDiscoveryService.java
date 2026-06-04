package com.example.petgo.service;

import com.example.petgo.dto.ProviderFilterOptionsResponse;
import com.example.petgo.dto.ProviderDetailServiceItemResponse;
import com.example.petgo.dto.ProviderListResponse;
import com.example.petgo.dto.ProviderSearchCriteria;

import java.util.List;

public interface ProviderDiscoveryService {
    ProviderListResponse findProviders(ProviderSearchCriteria criteria);

    List<ProviderDetailServiceItemResponse> findActiveServices(ProviderSearchCriteria criteria);

    ProviderFilterOptionsResponse getFilterOptions();
}
