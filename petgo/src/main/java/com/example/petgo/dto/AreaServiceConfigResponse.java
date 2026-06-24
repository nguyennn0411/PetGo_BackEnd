package com.example.petgo.dto;

import lombok.Builder;

@Builder
public record AreaServiceConfigResponse(
        Long id,
        Long areaId,
        String areaName,
        Long serviceId,
        String serviceName,
        Boolean active) {
}
