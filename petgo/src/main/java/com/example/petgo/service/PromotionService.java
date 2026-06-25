package com.example.petgo.service;

import com.example.petgo.dto.promotion.PromotionOptionsResponse;
import com.example.petgo.dto.promotion.PromotionRequest;
import com.example.petgo.dto.promotion.PromotionResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface PromotionService {
    List<PromotionResponse> listAdminPromotions(HttpServletRequest request, String status, String targetType);

    PromotionResponse createAdminPromotion(HttpServletRequest request, PromotionRequest requestBody);

    PromotionResponse updateAdminPromotion(HttpServletRequest request, Long id, PromotionRequest requestBody);

    PromotionResponse updateAdminPromotionStatus(HttpServletRequest request, Long id, Boolean active);

    void deleteAdminPromotion(HttpServletRequest request, Long id);

    PromotionOptionsResponse getAdminOptions(HttpServletRequest request);

}