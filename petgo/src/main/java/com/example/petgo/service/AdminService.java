package com.example.petgo.service;

import com.example.petgo.dto.ProviderDetailResponse;
import com.example.petgo.dto.ProviderResponse;
import com.example.petgo.dto.ProviderVerificationRequest;
import com.example.petgo.dto.ServiceCategoryRequest;
import com.example.petgo.dto.ServiceCategoryResponse;
import java.util.List;

public interface AdminService {
    List<ProviderResponse> getPendingProviders();

    List<ProviderResponse> getVerifiedProviders();

    ProviderDetailResponse getProviderDetail(Long providerId);

    void updateProviderStatus(ProviderVerificationRequest request);

    void updateProviderAccountStatus(ProviderVerificationRequest request);

    List<ServiceCategoryResponse> getAllCategories();
    ServiceCategoryResponse createCategory(ServiceCategoryRequest request);
    ServiceCategoryResponse updateCategory(Long id, ServiceCategoryRequest request);
    void deleteCategory(Long id);
}