package com.example.petgo.service;

import com.example.petgo.dto.*;
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

    List<HomeSliderResponse> getAllHomeSliders();

    HomeSliderResponse createHomeSlider(HomeSliderRequest request);

    HomeSliderResponse updateHomeSlider(Long id, HomeSliderRequest request);

    void deleteHomeSlider(Long id);

    HomeSliderResponse updateHomeSliderVisibility(Long id, Boolean active);
}