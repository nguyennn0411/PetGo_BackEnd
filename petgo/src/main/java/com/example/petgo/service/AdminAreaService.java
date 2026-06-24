package com.example.petgo.service;

import com.example.petgo.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AdminAreaService {

    List<AreaResponse> getAllAreas();

    AreaResponse createArea(AreaRequest request);

    AreaResponse updateArea(Long id, AreaRequest request);

    void deleteArea(Long id);

    List<AreaServiceConfigResponse> getAreaServiceConfigs(Long areaId);

    AreaServiceConfigResponse addAreaServiceConfig(Long areaId, AreaServiceConfigRequest request);

    AreaServiceConfigResponse updateAreaServiceConfig(Long areaId, Long configId, AreaServiceConfigRequest request);

    void removeAreaServiceConfig(Long areaId, Long configId);

    List<AreaScheduleResponse> getAreaSchedules(Long areaId);

    List<AreaScheduleResponse> updateAreaSchedules(Long areaId, List<AreaScheduleRequest> requests);

    List<AreaScheduleOverrideResponse> getAreaScheduleOverrides(Long areaId, String from, String to);

    AreaScheduleOverrideResponse upsertAreaScheduleOverride(Long areaId, AreaScheduleOverrideRequest request);

    void deleteAreaScheduleOverride(Long areaId, String date);

    List<ShippingFeeConfigResponse> getShippingFeeConfigs(Long areaId);

    ShippingFeeConfigResponse addShippingFeeConfig(Long areaId, ShippingFeeConfigRequest request);

    ShippingFeeConfigResponse updateShippingFeeConfig(Long areaId, Long configId, ShippingFeeConfigRequest request);

    void deleteShippingFeeConfig(Long areaId, Long configId);
}
