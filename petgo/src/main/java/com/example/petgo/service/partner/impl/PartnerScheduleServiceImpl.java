package com.example.petgo.service.partner.impl;

import com.example.petgo.dto.partner.PartnerBusinessHourRequest;
import com.example.petgo.dto.partner.PartnerScheduleResponse;
import com.example.petgo.dto.partner.PartnerWeeklyScheduleRequest;
import com.example.petgo.entity.ProviderAvailabilitySlot;
import com.example.petgo.entity.ProviderBusinessHour;
import com.example.petgo.entity.ProviderProfile;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.repository.ProviderAvailabilitySlotRepository;
import com.example.petgo.repository.ProviderBusinessHourRepository;
import com.example.petgo.service.partner.PartnerAccessService;
import com.example.petgo.service.partner.PartnerMappingSupport;
import com.example.petgo.service.partner.PartnerScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerScheduleServiceImpl implements PartnerScheduleService {

    private final PartnerAccessService partnerAccessService;
    private final PartnerMappingSupport mapper;
    private final ProviderBusinessHourRepository providerBusinessHourRepository;
    private final ProviderAvailabilitySlotRepository providerAvailabilitySlotRepository;

    @Override
    @Transactional(readOnly = true)
    public PartnerScheduleResponse getSchedule(HttpServletRequest request, String from, String to) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        List<ProviderAvailabilitySlot> slots = loadSlots(provider.getId(), from, to);
        return PartnerScheduleResponse.builder()
                .providerId(provider.getId())
                .weeklyHours(providerBusinessHourRepository.findByProvider_IdOrderByWeekdayAscIdAsc(provider.getId())
                        .stream()
                        .map(mapper::mapHour)
                        .toList())
                .slots(slots.stream().map(mapper::mapSlot).toList())
                .build();
    }

    @Override
    @Transactional
    public PartnerScheduleResponse updateWeeklySchedule(HttpServletRequest request,
            PartnerWeeklyScheduleRequest requestBody) {
        ProviderProfile provider = partnerAccessService.requirePartnerContext(request).provider();
        validateWeeklySchedule(requestBody);
        Map<Integer, ProviderBusinessHour> existingByWeekday = providerBusinessHourRepository
                .findByProvider_IdOrderByWeekdayAscIdAsc(provider.getId()).stream()
                .collect(
                        Collectors.toMap(ProviderBusinessHour::getWeekday, Function.identity(), (left, right) -> left));

        for (PartnerBusinessHourRequest hourRequest : requestBody.weeklyHours()) {
            ProviderBusinessHour hour = existingByWeekday.getOrDefault(hourRequest.weekday(),
                    new ProviderBusinessHour());
            if (hour.getId() == null) {
                hour.setProvider(provider);
                hour.setWeekday(hourRequest.weekday());
            }
            boolean closed = Boolean.TRUE.equals(hourRequest.closed());
            hour.setClosed(closed);
            hour.setOpensAt(closed ? null : parseTime(hourRequest.opensAt(), "Giờ mở cửa không hợp lệ."));
            hour.setClosesAt(closed ? null : parseTime(hourRequest.closesAt(), "Giờ đóng cửa không hợp lệ."));
            hour.setBreakStartsAt(
                    closed ? null : parseOptionalTime(hourRequest.breakStartsAt(), "Giờ bắt đầu nghỉ không hợp lệ."));
            hour.setBreakEndsAt(
                    closed ? null : parseOptionalTime(hourRequest.breakEndsAt(), "Giờ kết thúc nghỉ không hợp lệ."));
            if (!closed && hour.getOpensAt() != null && hour.getClosesAt() != null
                    && !hour.getOpensAt().isBefore(hour.getClosesAt())) {
                throw new BadRequestException("Giờ mở cửa phải trước giờ đóng cửa.");
            }
            providerBusinessHourRepository.save(hour);
        }

        return getSchedule(request, null, null);
    }

    private List<ProviderAvailabilitySlot> loadSlots(Long providerId, String from, String to) {
        if (mapper.normalizeBlank(from) == null || mapper.normalizeBlank(to) == null) {
            return providerAvailabilitySlotRepository.findByProvider_IdOrderBySlotDateAscStartTimeAscIdAsc(providerId)
                    .stream()
                    .limit(120)
                    .toList();
        }
        LocalDate fromDate = parseDate(from);
        LocalDate toDate = parseDate(to);
        if (toDate.isBefore(fromDate)) {
            throw new BadRequestException("Khoảng ngày lịch không hợp lệ.");
        }
        return providerAvailabilitySlotRepository
                .findByProvider_IdAndSlotDateBetweenOrderBySlotDateAscStartTimeAscIdAsc(providerId, fromDate, toDate)
                .stream()
                .sorted(Comparator.comparing(ProviderAvailabilitySlot::getSlotDate)
                        .thenComparing(ProviderAvailabilitySlot::getStartTime))
                .toList();
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, PartnerMappingSupport.ISO_DATE);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Ngày cần định dạng yyyy-MM-dd.");
        }
    }

    private void validateWeeklySchedule(PartnerWeeklyScheduleRequest requestBody) {
        if (requestBody == null || requestBody.weeklyHours() == null || requestBody.weeklyHours().isEmpty()) {
            throw new BadRequestException("Vui lòng cấu hình ít nhất một ngày.");
        }
        Set<Integer> weekdays = new HashSet<>();
        boolean hasOpenDay = false;
        for (PartnerBusinessHourRequest hourRequest : requestBody.weeklyHours()) {
            if (hourRequest.weekday() == null || hourRequest.weekday() < 1 || hourRequest.weekday() > 7) {
                throw new BadRequestException("weekday phải từ 1 đến 7.");
            }
            if (!weekdays.add(hourRequest.weekday())) {
                throw new BadRequestException("Không được cấu hình trùng ngày trong tuần.");
            }
            if (Boolean.TRUE.equals(hourRequest.closed())) {
                continue;
            }
            hasOpenDay = true;
            LocalTime opensAt = parseTime(hourRequest.opensAt(), "Giờ mở cửa không hợp lệ.");
            LocalTime closesAt = parseTime(hourRequest.closesAt(), "Giờ đóng cửa không hợp lệ.");
            if (!opensAt.isBefore(closesAt)) {
                throw new BadRequestException("Giờ mở cửa phải trước giờ đóng cửa.");
            }
            LocalTime breakStartsAt = parseOptionalTime(hourRequest.breakStartsAt(), "Giờ bắt đầu nghỉ không hợp lệ.");
            LocalTime breakEndsAt = parseOptionalTime(hourRequest.breakEndsAt(), "Giờ kết thúc nghỉ không hợp lệ.");
            if ((breakStartsAt == null) != (breakEndsAt == null)) {
                throw new BadRequestException("Cần nhập đủ giờ bắt đầu và kết thúc nghỉ giữa ca.");
            }
            if (breakStartsAt != null) {
                if (!breakStartsAt.isBefore(breakEndsAt)) {
                    throw new BadRequestException("Giờ bắt đầu nghỉ phải trước giờ kết thúc nghỉ.");
                }
                if (breakStartsAt.isBefore(opensAt) || breakEndsAt.isAfter(closesAt)) {
                    throw new BadRequestException("Giờ nghỉ phải nằm trong giờ mở cửa.");
                }
            }
        }
        if (!hasOpenDay) {
            throw new BadRequestException("Vui lòng mở ít nhất một ngày trong tuần.");
        }
    }

    private LocalTime parseTime(String value, String message) {
        LocalTime parsed = parseOptionalTime(value, message);
        if (parsed == null) {
            throw new BadRequestException(message);
        }
        return parsed;
    }

    private LocalTime parseOptionalTime(String value, String message) {
        String normalized = mapper.normalizeBlank(value);
        if (normalized == null)
            return null;
        try {
            return LocalTime.parse(normalized, PartnerMappingSupport.TIME_VIEW);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException(message);
        }
    }
}