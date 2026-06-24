package com.example.petgo.service.partner;

import com.example.petgo.dto.partner.PartnerReviewListResponse;
import com.example.petgo.dto.partner.PartnerReviewSummaryResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PartnerReviewManagementService {

    PartnerReviewListResponse listReviews(HttpServletRequest request, Integer rating, Long serviceId, String from,
            String to, String keyword, Integer page, Integer size);

    PartnerReviewSummaryResponse replyReview(HttpServletRequest request, Long reviewId, String reply);
}