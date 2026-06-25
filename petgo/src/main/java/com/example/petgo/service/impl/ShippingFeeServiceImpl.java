package com.example.petgo.service.impl;

import com.example.petgo.dto.ShippingFeeRequest;
import com.example.petgo.dto.ShippingFeeResponse;
import com.example.petgo.entity.Area;
import com.example.petgo.entity.ShippingFeeConfig;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.AreaRepository;
import com.example.petgo.repository.ShippingFeeConfigRepository;
import com.example.petgo.service.RoutingService;
import com.example.petgo.service.ShippingFeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingFeeServiceImpl implements ShippingFeeService {

    private final AreaRepository areaRepository;
    private final ShippingFeeConfigRepository shippingFeeConfigRepository;
    private final RoutingService routingService;

    @Override
    public ShippingFeeResponse calculateShippingFee(ShippingFeeRequest request) {
        Area area = areaRepository.findById(request.getAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu vực."));

        if (area.getPickupLatitude() == null || area.getPickupLongitude() == null) {
            throw new BadRequestException("Khu vực chưa cấu hình điểm đón.");
        }

        double distance = routingService.getDrivingDistanceKm(
                area.getPickupLatitude().doubleValue(),
                area.getPickupLongitude().doubleValue(),
                request.getPickupLatitude().doubleValue(),
                request.getPickupLongitude().doubleValue()
        );

        BigDecimal distanceKm = BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);

        List<ShippingFeeConfig> feeConfigs = shippingFeeConfigRepository
                .findByAreaIdAndActiveTrueOrderByFromKmAsc(request.getAreaId());

        if (feeConfigs.isEmpty()) {
            return ShippingFeeResponse.builder()
                    .distanceKm(distanceKm)
                    .shippingFee(BigDecimal.ZERO)
                    .message("Khu vực chưa cấu hình phí vận chuyển.")
                    .build();
        }

        for (ShippingFeeConfig config : feeConfigs) {
            boolean inRange = distanceKm.compareTo(config.getFromKm()) >= 0;
            if (config.getToKm() != null) {
                inRange = inRange && distanceKm.compareTo(config.getToKm()) < 0;
            }
            if (inRange) {
                return ShippingFeeResponse.builder()
                        .distanceKm(distanceKm)
                        .shippingFee(config.getFee())
                        .feeConfigId(config.getId())
                        .message("Tính phí vận chuyển thành công.")
                        .build();
            }
        }

        ShippingFeeConfig last = feeConfigs.get(feeConfigs.size() - 1);
        if (last.getToKm() == null) {
            return ShippingFeeResponse.builder()
                    .distanceKm(distanceKm)
                    .shippingFee(last.getFee())
                    .feeConfigId(last.getId())
                    .message("Tính phí vận chuyển thành công.")
                    .build();
        }

        throw new BadRequestException("Khoảng cách " + distanceKm + "km vượt quá phạm vi vận chuyển.");
    }

}
