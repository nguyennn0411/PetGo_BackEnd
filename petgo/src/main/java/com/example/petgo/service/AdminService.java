package com.example.petgo.service;

import com.example.petgo.dto.*;
import java.util.List;

public interface AdminService {
    List<ServiceCategoryResponse> getAllCategories();

    ServiceCategoryResponse createCategory(ServiceCategoryRequest request);

    ServiceCategoryResponse updateCategory(Long id, ServiceCategoryRequest request);

    void deleteCategory(Long id, ServiceCategoryDeleteRequest request);

    List<HomeSliderResponse> getAllHomeSliders();

    HomeSliderResponse createHomeSlider(HomeSliderRequest request);

    HomeSliderResponse updateHomeSlider(Long id, HomeSliderRequest request);

    void deleteHomeSlider(Long id);

    HomeSliderResponse updateHomeSliderVisibility(Long id, Boolean active);

}