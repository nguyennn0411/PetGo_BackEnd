package com.example.petgo.service;

import com.example.petgo.dto.ProviderFilterOptionsResponse;
import com.example.petgo.dto.ProviderListResponse;
import com.example.petgo.dto.ProviderSearchCriteria;

public interface ProviderDiscoveryService {
    ProviderListResponse findProviders(ProviderSearchCriteria criteria);
    ProviderFilterOptionsResponse getFilterOptions();
}
