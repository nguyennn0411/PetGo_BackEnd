package com.example.petgo.service.impl;

import com.example.petgo.dto.*;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.*;
import com.example.petgo.service.AdminAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAreaServiceImpl implements AdminAreaService {

    private final AreaRepository areaRepository;
    private final AreaServiceConfigRepository areaServiceConfigRepository;
    private final AreaScheduleRepository areaScheduleRepository;
    private final AreaScheduleOverrideRepository areaScheduleOverrideRepository;
    private final ShippingFeeConfigRepository shippingFeeConfigRepository;
    private final CatalogServiceRepository catalogServiceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AreaResponse> getAllAreas() {
        return areaRepository.findAllByOrderByNameAsc().stream()
                .map(this::toAreaResponse)
                .toList();
    }

    @Override
    @Transactional
    public AreaResponse createArea(AreaRequest request) {
        Area area = new Area();
        mapAreaRequest(request, area);
        return toAreaResponse(areaRepository.save(area));
    }

    @Override
    @Transactional
    public AreaResponse updateArea(Long id, AreaRequest request) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu vực."));
        mapAreaRequest(request, area);
        return toAreaResponse(areaRepository.save(area));
    }

    @Override
    @Transactional
    public void deleteArea(Long id) {
        if (!areaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy khu vực.");
        }
        areaServiceConfigRepository.deleteByAreaId(id);
        areaScheduleRepository.deleteByAreaId(id);
        areaScheduleOverrideRepository.deleteByAreaId(id);
        shippingFeeConfigRepository.deleteByAreaId(id);
        areaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AreaServiceConfigResponse> getAreaServiceConfigs(Long areaId) {
        if (!areaRepository.existsById(areaId)) {
            throw new ResourceNotFoundException("Không tìm thấy khu vực.");
        }
        return areaServiceConfigRepository.findByAreaId(areaId).stream()
                .map(this::toConfigResponse)
                .toList();
    }

    @Override
    @Transactional
    public AreaServiceConfigResponse addAreaServiceConfig(Long areaId, AreaServiceConfigRequest request) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu vực."));
        CatalogService service = catalogServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ."));

        if (areaServiceConfigRepository.existsByAreaIdAndServiceId(areaId, request.getServiceId())) {
            throw new BadRequestException("Dịch vụ đã được cấu hình trong khu vực này.");
        }

        AreaServiceConfig config = new AreaServiceConfig();
        config.setArea(area);
        config.setService(service);
        config.setActive(request.getActive());

        return toConfigResponse(areaServiceConfigRepository.save(config));
    }

    @Override
    @Transactional
    public AreaServiceConfigResponse updateAreaServiceConfig(Long areaId, Long configId, AreaServiceConfigRequest request) {
        AreaServiceConfig config = areaServiceConfigRepository.findById(configId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cấu hình."));
        if (!config.getArea().getId().equals(areaId)) {
            throw new BadRequestException("Cấu hình không thuộc khu vực này.");
        }

        if (request.getServiceId() != null) {
            CatalogService service = catalogServiceRepository.findById(request.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ."));
            config.setService(service);
        }
        if (request.getActive() != null) config.setActive(request.getActive());

        return toConfigResponse(areaServiceConfigRepository.save(config));
    }

    @Override
    @Transactional
    public void removeAreaServiceConfig(Long areaId, Long configId) {
        AreaServiceConfig config = areaServiceConfigRepository.findById(configId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cấu hình."));
        if (!config.getArea().getId().equals(areaId)) {
            throw new BadRequestException("Cấu hình không thuộc khu vực này.");
        }
        areaServiceConfigRepository.delete(config);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AreaScheduleResponse> getAreaSchedules(Long areaId) {
        if (!areaRepository.existsById(areaId)) {
            throw new ResourceNotFoundException("Không tìm thấy khu vực.");
        }
        return areaScheduleRepository.findByAreaIdOrderByDayOfWeekAsc(areaId).stream()
                .map(this::toScheduleResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<AreaScheduleResponse> updateAreaSchedules(Long areaId, List<AreaScheduleRequest> requests) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu vực."));

        List<AreaSchedule> existing = areaScheduleRepository.findByAreaIdOrderByDayOfWeekAsc(areaId);
        areaScheduleRepository.deleteAll(existing);

        List<AreaSchedule> schedules = new ArrayList<>();
        for (AreaScheduleRequest req : requests) {
            if (req.getDayOfWeek() < 0 || req.getDayOfWeek() > 6) {
                throw new BadRequestException("Thứ trong tuần không hợp lệ (0-6).");
            }
            if (req.getCloseTime() != null && req.getOpenTime() != null
                    && !req.getCloseTime().isAfter(req.getOpenTime())) {
                throw new BadRequestException("Giờ đóng cửa phải sau giờ mở cửa.");
            }
            AreaSchedule schedule = new AreaSchedule();
            schedule.setArea(area);
            schedule.setDayOfWeek(req.getDayOfWeek());
            schedule.setOpenTime(req.getOpenTime());
            schedule.setCloseTime(req.getCloseTime());
            schedule.setActive(req.getActive());
            schedules.add(schedule);
        }

        return areaScheduleRepository.saveAll(schedules).stream()
                .map(this::toScheduleResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AreaScheduleOverrideResponse> getAreaScheduleOverrides(Long areaId, String from, String to) {
        if (!areaRepository.existsById(areaId)) {
            throw new ResourceNotFoundException("Không tìm thấy khu vực.");
        }
        LocalDate fromDate = from != null ? LocalDate.parse(from) : LocalDate.now();
        LocalDate toDate = to != null ? LocalDate.parse(to) : fromDate.plusMonths(1);
        return areaScheduleOverrideRepository
                .findByAreaIdAndOverrideDateBetweenOrderByOverrideDateAsc(areaId, fromDate, toDate)
                .stream().map(this::toOverrideResponse).toList();
    }

    @Override
    @Transactional
    public AreaScheduleOverrideResponse upsertAreaScheduleOverride(Long areaId, AreaScheduleOverrideRequest request) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu vực."));

        if (request.getOverrideDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Không thể cấu hình ngày trong quá khứ.");
        }

        AreaScheduleOverride override = areaScheduleOverrideRepository
                .findByAreaIdAndOverrideDate(areaId, request.getOverrideDate())
                .orElseGet(() -> {
                    AreaScheduleOverride o = new AreaScheduleOverride();
                    o.setArea(area);
                    o.setOverrideDate(request.getOverrideDate());
                    return o;
                });

        if (request.getOpenTime() != null) override.setOpenTime(request.getOpenTime());
        if (request.getCloseTime() != null) override.setCloseTime(request.getCloseTime());
        override.setClosed(request.getClosed());
        override.setReason(request.getReason());

        return toOverrideResponse(areaScheduleOverrideRepository.save(override));
    }

    @Override
    @Transactional
    public void deleteAreaScheduleOverride(Long areaId, String date) {
        LocalDate overrideDate = LocalDate.parse(date);
        AreaScheduleOverride override = areaScheduleOverrideRepository
                .findByAreaIdAndOverrideDate(areaId, overrideDate)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch override cho ngày này."));
        areaScheduleOverrideRepository.delete(override);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingFeeConfigResponse> getShippingFeeConfigs(Long areaId) {
        if (!areaRepository.existsById(areaId)) {
            throw new ResourceNotFoundException("Không tìm thấy khu vực.");
        }
        return shippingFeeConfigRepository.findByAreaIdOrderByFromKmAsc(areaId).stream()
                .map(this::toShippingFeeResponse)
                .toList();
    }

    @Override
    @Transactional
    public ShippingFeeConfigResponse addShippingFeeConfig(Long areaId, ShippingFeeConfigRequest request) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu vực."));

        if (request.getFromKm().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Số km bắt đầu không được âm.");
        }
        if (request.getToKm() != null && request.getToKm().compareTo(request.getFromKm()) <= 0) {
            throw new BadRequestException("Số km kết thúc phải lớn hơn số km bắt đầu.");
        }
        if (request.getFee().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Phí vận chuyển phải lớn hơn 0.");
        }

        ShippingFeeConfig config = new ShippingFeeConfig();
        config.setArea(area);
        config.setFromKm(request.getFromKm());
        config.setToKm(request.getToKm());
        config.setFee(request.getFee());
        config.setActive(request.getActive());

        return toShippingFeeResponse(shippingFeeConfigRepository.save(config));
    }

    @Override
    @Transactional
    public ShippingFeeConfigResponse updateShippingFeeConfig(Long areaId, Long configId, ShippingFeeConfigRequest request) {
        ShippingFeeConfig config = shippingFeeConfigRepository.findById(configId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cấu hình phí vận chuyển."));
        if (!config.getArea().getId().equals(areaId)) {
            throw new BadRequestException("Cấu hình không thuộc khu vực này.");
        }

        if (request.getFromKm() != null) {
            if (request.getFromKm().compareTo(java.math.BigDecimal.ZERO) < 0) {
                throw new BadRequestException("Số km bắt đầu không được âm.");
            }
            config.setFromKm(request.getFromKm());
        }
        if (request.getToKm() != null) config.setToKm(request.getToKm());
        if (request.getFee() != null) {
            if (request.getFee().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Phí vận chuyển phải lớn hơn 0.");
            }
            config.setFee(request.getFee());
        }
        if (request.getActive() != null) config.setActive(request.getActive());

        return toShippingFeeResponse(shippingFeeConfigRepository.save(config));
    }

    @Override
    @Transactional
    public void deleteShippingFeeConfig(Long areaId, Long configId) {
        ShippingFeeConfig config = shippingFeeConfigRepository.findById(configId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cấu hình phí vận chuyển."));
        if (!config.getArea().getId().equals(areaId)) {
            throw new BadRequestException("Cấu hình không thuộc khu vực này.");
        }
        shippingFeeConfigRepository.delete(config);
    }

    private void mapAreaRequest(AreaRequest request, Area area) {
        area.setName(request.getName().trim());
        area.setWardCode(request.getWardCode());
        area.setDistrictCode(request.getDistrictCode());
        area.setProvinceCode(request.getProvinceCode());
        area.setPickupLatitude(request.getPickupLatitude());
        area.setPickupLongitude(request.getPickupLongitude());
        area.setPickupAddress(request.getPickupAddress());
        area.setPickupPhone(request.getPickupPhone());
        area.setPickupInstructions(request.getPickupInstructions());
        if (request.getShortSlots() != null) area.setShortSlots(request.getShortSlots());
        if (request.getLongSlots() != null) area.setLongSlots(request.getLongSlots());
    }

    private AreaResponse toAreaResponse(Area area) {
        return AreaResponse.builder()
                .id(area.getId())
                .name(area.getName())
                .wardCode(area.getWardCode())
                .districtCode(area.getDistrictCode())
                .provinceCode(area.getProvinceCode())
                .pickupLatitude(area.getPickupLatitude())
                .pickupLongitude(area.getPickupLongitude())
                .pickupAddress(area.getPickupAddress())
                .pickupPhone(area.getPickupPhone())
                .pickupInstructions(area.getPickupInstructions())
                .shortSlots(area.getShortSlots())
                .longSlots(area.getLongSlots())
                .createdAt(area.getCreatedAt())
                .updatedAt(area.getUpdatedAt())
                .build();
    }

    private AreaServiceConfigResponse toConfigResponse(AreaServiceConfig config) {
        return AreaServiceConfigResponse.builder()
                .id(config.getId())
                .areaId(config.getArea().getId())
                .areaName(config.getArea().getName())
                .serviceId(config.getService().getId())
                .serviceName(config.getService().getName())
                .active(config.getActive())
                .build();
    }

    private AreaScheduleResponse toScheduleResponse(AreaSchedule schedule) {
        return AreaScheduleResponse.builder()
                .id(schedule.getId())
                .areaId(schedule.getArea().getId())
                .dayOfWeek(schedule.getDayOfWeek())
                .openTime(schedule.getOpenTime())
                .closeTime(schedule.getCloseTime())
                .active(schedule.getActive())
                .build();
    }

    private AreaScheduleOverrideResponse toOverrideResponse(AreaScheduleOverride override) {
        return AreaScheduleOverrideResponse.builder()
                .id(override.getId())
                .areaId(override.getArea().getId())
                .overrideDate(override.getOverrideDate())
                .openTime(override.getOpenTime())
                .closeTime(override.getCloseTime())
                .closed(override.getClosed())
                .reason(override.getReason())
                .build();
    }

    private ShippingFeeConfigResponse toShippingFeeResponse(ShippingFeeConfig config) {
        return ShippingFeeConfigResponse.builder()
                .id(config.getId())
                .areaId(config.getArea().getId())
                .fromKm(config.getFromKm())
                .toKm(config.getToKm())
                .fee(config.getFee())
                .active(config.getActive())
                .build();
    }
}
