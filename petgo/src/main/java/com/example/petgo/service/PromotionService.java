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

    PromotionOptionsResponse getAdminOptions(HttpServletRequest request);

    List<PromotionResponse> listPartnerPromotions(HttpServletRequest request, String status, String targetType);

    PromotionResponse createPartnerPromotion(HttpServletRequest request, PromotionRequest requestBody);

    PromotionResponse updatePartnerPromotion(HttpServletRequest request, Long id, PromotionRequest requestBody);

    PromotionResponse updatePartnerPromotionStatus(HttpServletRequest request, Long id, Boolean active);

    PromotionOptionsResponse getPartnerOptions(HttpServletRequest request);
}