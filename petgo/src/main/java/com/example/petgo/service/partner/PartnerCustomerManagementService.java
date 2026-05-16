package com.example.petgo.service.partner;

import com.example.petgo.dto.partner.PartnerCustomerDetailResponse;
import com.example.petgo.dto.partner.PartnerCustomerListResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PartnerCustomerManagementService {

    PartnerCustomerListResponse listCustomers(HttpServletRequest request, String keyword, String status,
            Integer page, Integer size);

    PartnerCustomerDetailResponse getCustomerDetail(HttpServletRequest request, Long customerUserId);
}