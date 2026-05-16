package com.example.petgo.service.partner;

import com.example.petgo.dto.partner.AdminPartnerServiceReviewRequest;
import com.example.petgo.dto.partner.PartnerServiceCategoryResponse;
import com.example.petgo.dto.partner.PartnerServiceChangeRequestResponse;
import com.example.petgo.dto.partner.PartnerServiceChangeRequestUpsertRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface PartnerServiceChangeRequestService {

    List<PartnerServiceCategoryResponse> listCategoryOptions(HttpServletRequest request);

    List<PartnerServiceChangeRequestResponse> listMyRequests(HttpServletRequest request);

    PartnerServiceChangeRequestResponse saveDraft(HttpServletRequest request,
            PartnerServiceChangeRequestUpsertRequest requestBody);

    PartnerServiceChangeRequestResponse updateDraft(HttpServletRequest request, Long requestId,
            PartnerServiceChangeRequestUpsertRequest requestBody);

    PartnerServiceChangeRequestResponse submitNewRequest(HttpServletRequest request,
            PartnerServiceChangeRequestUpsertRequest requestBody);

    PartnerServiceChangeRequestResponse submitDraft(HttpServletRequest request, Long requestId);

    void deleteDraft(HttpServletRequest request, Long requestId);

    PartnerServiceChangeRequestResponse copyFromService(HttpServletRequest request, Long providerServiceId);

    PartnerServiceChangeRequestResponse copyFromRequest(HttpServletRequest request, Long requestId);

    List<PartnerServiceChangeRequestResponse> listAdminRequests(HttpServletRequest request, String status);

    PartnerServiceChangeRequestResponse getAdminRequestDetail(HttpServletRequest request, Long requestId);

    PartnerServiceChangeRequestResponse approve(HttpServletRequest request, Long requestId,
            AdminPartnerServiceReviewRequest requestBody);

    PartnerServiceChangeRequestResponse reject(HttpServletRequest request, Long requestId,
            AdminPartnerServiceReviewRequest requestBody);
}