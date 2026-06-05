package com.example.petgo.service.partner;

import com.example.petgo.dto.partner.PartnerServiceRequest;
import com.example.petgo.dto.partner.PartnerServiceResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface PartnerServiceManagementService {
    List<PartnerServiceResponse> listServices(HttpServletRequest request);

    PartnerServiceResponse createService(HttpServletRequest request, PartnerServiceRequest requestBody);

    PartnerServiceResponse updateService(HttpServletRequest request, Long providerServiceId,
            PartnerServiceRequest requestBody);

    PartnerServiceResponse updateServiceStatus(HttpServletRequest request, Long providerServiceId, Boolean active);

    PartnerServiceResponse archiveService(HttpServletRequest request, Long providerServiceId);
}