package com.example.petgo.service.impl;

import com.example.petgo.dto.AvailabilityDateResponse;
import com.example.petgo.dto.AvailabilitySlotResponse;
import com.example.petgo.entity.*;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.*;
import com.example.petgo.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AreaRepository areaRepository;
    private final AreaServiceConfigRepository areaServiceConfigRepository;
    private final AreaScheduleRepository areaScheduleRepository;
    private final AreaScheduleOverrideRepository areaScheduleOverrideRepository;
    private final ShippingBookingRepository shippingBookingRepository;
    private final CatalogServiceRepository catalogServiceRepository;

    private static final int MINIMUM_LEAD_TIME_MINUTES = 60;
    private static final List<String> ACTIVE_STATUSES = List.of("PENDING", "CONFIRMED", "IN_PROGRESS");

    @Override
    public List<AvailabilityDateResponse> getAvailableDates(Long areaId, Long serviceId, String from, Integer days) {
        if (!areaRepository.existsById(areaId)) {
            throw new ResourceNotFoundException("Không tìm thấy khu vực.");
        }
        validateServiceInArea(areaId, serviceId);

        if (days == null || days < 1) days = 14;
        if (days > 90) days = 90;

        LocalDate fromDate = from != null ? LocalDate.parse(from) : LocalDate.now();
        LocalDate toDate = fromDate.plusDays(days);

        List<AreaSchedule> schedules = areaScheduleRepository.findByAreaIdAndActiveTrue(areaId);
        List<AreaScheduleOverride> overrides = areaScheduleOverrideRepository
                .findByAreaIdAndOverrideDateBetweenOrderByOverrideDateAsc(areaId, fromDate, toDate);

        Map<LocalDate, AreaScheduleOverride> overrideMap = new HashMap<>();
        for (AreaScheduleOverride o : overrides) {
            overrideMap.put(o.getOverrideDate(), o);
        }

        Map<Integer, AreaSchedule> scheduleMap = new HashMap<>();
        for (AreaSchedule s : schedules) {
            scheduleMap.put(s.getDayOfWeek(), s);
        }

        List<AvailabilityDateResponse> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (LocalDate date = fromDate; date.isBefore(toDate); date = date.plusDays(1)) {
            if (date.isBefore(today)) {
                result.add(AvailabilityDateResponse.builder()
                        .date(date).status("PAST").reason("Ngày trong quá khứ").build());
                continue;
            }

            AreaScheduleOverride override = overrideMap.get(date);

            if (override != null && Boolean.TRUE.equals(override.getClosed())) {
                result.add(AvailabilityDateResponse.builder()
                        .date(date).status("CLOSED")
                        .reason(override.getReason() != null ? override.getReason() : "Ngày đóng cửa").build());
                continue;
            }

            int dow = date.getDayOfWeek().getValue() % 7;
            AreaSchedule schedule = scheduleMap.get(dow);

            if (override != null) {
                if (override.getOpenTime() != null && override.getCloseTime() != null) {
                    result.add(AvailabilityDateResponse.builder().date(date).status("AVAILABLE").build());
                } else {
                    result.add(AvailabilityDateResponse.builder()
                            .date(date).status("CLOSED").reason("Ngày đóng cửa").build());
                }
                continue;
            }

            if (schedule == null) {
                result.add(AvailabilityDateResponse.builder()
                        .date(date).status("NOT_CONFIGURED").reason("Chưa có lịch làm việc").build());
                continue;
            }

            if (date.equals(today) && schedule.getCloseTime().isBefore(LocalTime.now().plusMinutes(MINIMUM_LEAD_TIME_MINUTES))) {
                result.add(AvailabilityDateResponse.builder()
                        .date(date).status("FULL").reason("Hôm nay đã hết giờ nhận booking").build());
                continue;
            }

            result.add(AvailabilityDateResponse.builder().date(date).status("AVAILABLE").build());
        }

        return result;
    }

    @Override
    public List<AvailabilitySlotResponse> getAvailableSlots(Long areaId, Long serviceId, String date) {
        if (!areaRepository.existsById(areaId)) {
            throw new ResourceNotFoundException("Không tìm thấy khu vực.");
        }
        validateServiceInArea(areaId, serviceId);

        LocalDate targetDate = LocalDate.parse(date);
        if (targetDate.isBefore(LocalDate.now())) {
            return List.of();
        }

        CatalogService service = catalogServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ."));

        int durationMinutes = service.getDefaultDurationMinutes() != null ? service.getDefaultDurationMinutes() : 60;
        String bookingType = service.getBookingType() != null ? service.getBookingType() : "SHORT";

        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu vực."));

        int maxSlots = "LONG".equals(bookingType) ? area.getLongSlots() : area.getShortSlots();

        AreaScheduleOverride override = areaScheduleOverrideRepository
                .findByAreaIdAndOverrideDate(areaId, targetDate).orElse(null);

        if (override != null && Boolean.TRUE.equals(override.getClosed())) {
            return List.of();
        }

        LocalTime openTime;
        LocalTime closeTime;

        if (override != null && override.getOpenTime() != null && override.getCloseTime() != null) {
            openTime = override.getOpenTime();
            closeTime = override.getCloseTime();
        } else {
            int dow = targetDate.getDayOfWeek().getValue() % 7;
            AreaSchedule schedule = areaScheduleRepository
                    .findByAreaIdAndDayOfWeek(areaId, dow).orElse(null);
            if (schedule == null || !Boolean.TRUE.equals(schedule.getActive())) {
                return List.of();
            }
            openTime = schedule.getOpenTime();
            closeTime = schedule.getCloseTime();
        }

        LocalTime now = LocalTime.now();
        boolean isToday = targetDate.equals(LocalDate.now());

        List<LocalTime> bookedStarts = new ArrayList<>();
        List<LocalTime> bookedEnds = new ArrayList<>();

        if ("SHORT".equals(bookingType)) {
            List<ShippingBooking> existing = shippingBookingRepository
                    .findByAreaIdAndAppointmentDateAndBookingTypeAndStatusIn(
                            areaId, targetDate, "SHORT", ACTIVE_STATUSES);
            for (ShippingBooking b : existing) {
                if (b.getStartTime() != null && b.getEndTime() != null) {
                    bookedStarts.add(b.getStartTime());
                    bookedEnds.add(b.getEndTime());
                }
            }
        }

        List<ShippingBooking> existingLong = shippingBookingRepository
                .findByAreaIdAndAppointmentDateAndBookingTypeAndStatusIn(
                        areaId, targetDate, "LONG", ACTIVE_STATUSES);

        List<AvailabilitySlotResponse> result = new ArrayList<>();
        LocalTime slotStart = openTime;

        while (!slotStart.isAfter(closeTime)) {
            LocalTime slotEnd = slotStart.plusMinutes(durationMinutes);

            if (slotEnd.isAfter(closeTime)) break;

            if (isToday && slotStart.isBefore(now.plusMinutes(MINIMUM_LEAD_TIME_MINUTES))) {
                result.add(AvailabilitySlotResponse.builder()
                        .startTime(slotStart).endTime(slotEnd)
                        .status("PAST").reason("Đã qua thời gian đặt tối thiểu").build());
                slotStart = slotStart.plusMinutes(30);
                continue;
            }

            if ("SHORT".equals(bookingType)) {
                boolean overlap = false;
                for (int i = 0; i < bookedStarts.size(); i++) {
                    if (slotStart.isBefore(bookedEnds.get(i)) && slotEnd.isAfter(bookedStarts.get(i))) {
                        overlap = true;
                        break;
                    }
                }
                if (overlap) {
                    result.add(AvailabilitySlotResponse.builder()
                            .startTime(slotStart).endTime(slotEnd)
                            .status("FULL").reason("Khung giờ đã có booking").build());
                    slotStart = slotStart.plusMinutes(30);
                    continue;
                }
            }

            long concurrentCount = 0;
            for (ShippingBooking b : existingLong) {
                if (b.getStartTime() != null && b.getEndTime() != null) {
                    if (slotStart.isBefore(b.getEndTime()) && slotEnd.isAfter(b.getStartTime())) {
                        concurrentCount++;
                    }
                }
            }
            if (concurrentCount >= maxSlots) {
                result.add(AvailabilitySlotResponse.builder()
                        .startTime(slotStart).endTime(slotEnd)
                        .status("FULL").reason("Đã hết chỗ cho khung giờ này").build());
                slotStart = slotStart.plusMinutes(30);
                continue;
            }

            result.add(AvailabilitySlotResponse.builder()
                    .startTime(slotStart).endTime(slotEnd)
                    .status("AVAILABLE").build());

            slotStart = slotStart.plusMinutes(30);
        }

        return result;
    }

    private void validateServiceInArea(Long areaId, Long serviceId) {
        if (!areaServiceConfigRepository.existsByAreaIdAndServiceId(areaId, serviceId)) {
            throw new ResourceNotFoundException("Dịch vụ không khả dụng trong khu vực này.");
        }
    }
}
