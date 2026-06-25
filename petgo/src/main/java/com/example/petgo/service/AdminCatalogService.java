package com.example.petgo.service;

import com.example.petgo.dto.AdminServiceCreateRequest;
import com.example.petgo.dto.AdminServiceUpdateRequest;
import com.example.petgo.dto.CatalogServiceResponse;

import java.util.List;

public interface AdminCatalogService {

    List<CatalogServiceResponse> getAllServices();

    CatalogServiceResponse getServiceById(Long id);

    CatalogServiceResponse createService(AdminServiceCreateRequest request);

    CatalogServiceResponse updateService(Long id, AdminServiceUpdateRequest request);

    void deleteService(Long id);
}
