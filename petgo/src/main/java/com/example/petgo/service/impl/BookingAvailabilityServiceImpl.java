package com.example.petgo.service.impl;

import com.example.petgo.dto.*;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.*;
import com.example.petgo.service.BookingAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingAvailabilityServiceImpl implements BookingAvailabilityService {

    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_VIEW = DateTimeFormatter.ofPattern("HH:mm");
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final long AVAILABILITY_CACHE_TTL_SECONDS = 45;

    private final UserRepository userRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final ProviderBusinessHourRepository providerBusinessHourRepository;
    private final ProviderScheduleExceptionRepository providerScheduleExceptionRepository;
    private final BookingRepository bookingRepository;
    private final BookingLockRepository bookingLockRepository;
    private final Map<String, AvailabilityCacheEntry> availabilityCache = new ConcurrentHashMap<>();

    @Value("${app.bookings.default-timezone:Asia/Ho_Chi_Minh}")
    private String defaultTimezone;

    @Value("${app.bookings.slot-step-minutes:15}")
    private int slotStepMinutes;

    @Value("${app.bookings.lock-minutes:5}")
    private int lockMinutes;

    @Override
    @Transactional(readOnly = true)
    public BookingAvailabilityResponse getAvailability(Long providerId, Long providerServiceId, LocalDate date,
            Integer durationMinutes) {
        ProviderProfile provider = providerProfileRepository.findActiveById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp"));
        ProviderService service = providerServiceRepository.findActiveDetailById(providerServiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ"));
        validateServiceBelongsToProvider(provider, service);
        ZoneId providerZone = resolveZone(provider);
        LocalDate today = LocalDate.now(providerZone);
        LocalDate resolvedDate = Optional.ofNullable(date).orElse(today);
        if (resolvedDate.isBefore(today)) {
            return BookingAvailabilityResponse.builder()
                    .providerId(provider.getId())
                    .providerServiceId(service.getId())
                    .date(resolvedDate.format(ISO_DATE))
                    .timezone(providerZone.getId())
                    .durationMinutes(resolveDuration(service, durationMinutes))
                    .bufferAfterMinutes(resolveBufferAfter(service))
                    .maxConcurrent(0)
                    .slots(List.of())
                    .build();
        }
        String cacheKey = buildAvailabilityCacheKey(provider.getId(), service.getId(), resolvedDate, durationMinutes);
        AvailabilityCacheEntry cached = availabilityCache.get(cacheKey);
        if (cached != null && cached.expiresAtUtc().isAfter(LocalDateTime.now(UTC))) {
            return cached.response();
        }
        ProviderBusinessHour hour = resolveBusinessHour(provider.getId(), resolvedDate);
        int duration = resolveDuration(service, durationMinutes);
        int buffer = resolveBufferAfter(service);
        List<ProviderScheduleException> exceptions = providerScheduleExceptionRepository
                .findByProvider_IdAndLocalDateOrderByStartsAtLocalAscIdAsc(provider.getId(), resolvedDate);
        int maxConcurrent = resolveMaxConcurrent(hour, exceptions, null);

        List<BookingSlotOptionResponse> slots = new ArrayList<>();
        LocalTime cursor = hour.getOpensAt();
        while (!cursor.plusMinutes(duration).isAfter(hour.getClosesAt())) {
            LocalTime serviceEnd = cursor.plusMinutes(duration);
            LocalTime occupiedEnd = serviceEnd.plusMinutes(buffer);
            int slotMaxConcurrent = resolveMaxConcurrent(hour, exceptions, cursor);
            if (slotMaxConcurrent > 0 && !overlapsBreak(cursor, occupiedEnd, hour)
                    && !isBlockedByException(cursor, occupiedEnd, exceptions)) {
                int occupied = countOccupied(provider.getId(), service.getId(), resolvedDate, cursor, occupiedEnd,
                        null);
                if (occupied < slotMaxConcurrent) {
                    slots.add(BookingSlotOptionResponse.builder()
                            .slotId(null)
                            .providerServiceId(service.getId())
                            .serviceName(firstNonBlank(service.getCustomName(),
                                    service.getService() != null ? service.getService().getName() : null))
                            .date(resolvedDate.format(ISO_DATE))
                            .startTime(cursor.format(TIME_VIEW))
                            .endTime(serviceEnd.format(TIME_VIEW))
                            .label(cursor.format(TIME_VIEW))
                            .capacityRemaining(slotMaxConcurrent - occupied)
                            .selected(false)
                            .build());
                }
            }
            cursor = cursor.plusMinutes(Math.max(slotStepMinutes, 5));
        }

        BookingAvailabilityResponse response = BookingAvailabilityResponse.builder()
                .providerId(provider.getId())
                .providerServiceId(service.getId())
                .date(resolvedDate.format(ISO_DATE))
                .timezone(providerZone.getId())
                .durationMinutes(duration)
                .bufferAfterMinutes(buffer)
                .maxConcurrent(maxConcurrent)
                .slots(slots)
                .build();
        availabilityCache.put(cacheKey,
                new AvailabilityCacheEntry(response,
                        LocalDateTime.now(UTC).plusSeconds(AVAILABILITY_CACHE_TTL_SECONDS)));
        return response;
    }

    @Override
    @Transactional
    public BookingLockResponse createLock(BookingLockRequest request) {
        User owner = userRepository.findById(request.ownerUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng đặt lịch"));
        ProviderProfile provider = providerProfileRepository.findActiveById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp"));
        ProviderService service = providerServiceRepository.findActiveDetailById(request.providerServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ"));
        validateServiceBelongsToProvider(provider, service);
        validateNotPast(provider, request.appointmentDate(), request.startTime());
        ProviderBusinessHour hour = resolveBusinessHour(provider.getId(), request.appointmentDate());
        List<ProviderScheduleException> exceptions = providerScheduleExceptionRepository
                .findByProvider_IdAndLocalDateOrderByStartsAtLocalAscIdAsc(provider.getId(), request.appointmentDate());
        int duration = resolveDuration(service, request.durationMinutes());
        int buffer = resolveBufferAfter(service);
        LocalTime start = request.startTime();
        LocalTime serviceEnd = start.plusMinutes(duration);
        LocalTime occupiedEnd = serviceEnd.plusMinutes(buffer);
        if (start.isBefore(hour.getOpensAt()) || serviceEnd.isAfter(hour.getClosesAt())
                || overlapsBreak(start, occupiedEnd, hour) || isBlockedByException(start, occupiedEnd, exceptions)) {
            throw new BadRequestException("Khung giờ đã chọn nằm ngoài lịch nhận booking của shop");
        }
        int maxConcurrent = resolveMaxConcurrent(hour, exceptions, start);
        if (maxConcurrent <= 0) {
            throw new BadRequestException("Shop không nhận booking vào khung giờ này");
        }
        int occupied = countOccupied(provider.getId(), service.getId(), request.appointmentDate(), start, occupiedEnd,
                owner.getId());
        if (occupied >= maxConcurrent) {
            log.warn(
                    "booking.slot_lock_rejected providerId={} serviceId={} date={} start={} end={} occupied={} maxConcurrent={}",
                    provider.getId(), service.getId(), request.appointmentDate(), start, occupiedEnd, occupied,
                    maxConcurrent);
            throw new BadRequestException("Slot đã hết chỗ, vui lòng chọn giờ khác");
        }

        BookingLock lock = new BookingLock();
        lock.setProvider(provider);
        lock.setProviderService(service);
        lock.setUser(owner);
        lock.setAppointmentDate(request.appointmentDate());
        lock.setStartTime(start);
        lock.setEndTime(occupiedEnd);
        lock.setDurationMinutes(duration);
        lock.setBufferAfterMinutes(buffer);
        lock.setExpiresAtUtc(LocalDateTime.now(UTC).plusMinutes(Math.max(lockMinutes, 1)));
        lock.setStatus("ACTIVE");
        BookingLock saved = bookingLockRepository.save(lock);
        invalidateAvailabilityCache(provider.getId(), service.getId(), request.appointmentDate());

        return BookingLockResponse.builder()
                .lockId(saved.getId())
                .ownerUserId(owner.getId())
                .providerId(provider.getId())
                .providerServiceId(service.getId())
                .appointmentDate(request.appointmentDate().format(ISO_DATE))
                .startTime(start.format(TIME_VIEW))
                .endTime(serviceEnd.format(TIME_VIEW))
                .expiresAtUtc(saved.getExpiresAtUtc().toString())
                .expiresInSeconds(Math.max(lockMinutes, 1) * 60)
                .status(saved.getStatus())
                .message("Đã giữ slot trong " + Math.max(lockMinutes, 1) + " phút")
                .build();
    }

    private int countOccupied(Long providerId, Long serviceId, LocalDate date, LocalTime start, LocalTime end,
            Long lockOwnerUserId) {
        long bookingCount = bookingRepository.countActiveOverlappingBookings(providerId, serviceId, date, start, end);
        long lockCount = bookingLockRepository.countActiveOverlappingLocks(providerId, serviceId, date, start, end,
                LocalDateTime.now(UTC), lockOwnerUserId);
        return Math.toIntExact(bookingCount + lockCount);
    }

    public void invalidateAvailabilityCache(Long providerId, Long providerServiceId, LocalDate date) {
        String prefix = providerId + ":" + (providerServiceId != null ? providerServiceId : "*") + ":"
                + (date != null ? date.format(ISO_DATE) : "");
        availabilityCache.keySet().removeIf(key -> providerServiceId != null ? key.startsWith(prefix)
                : key.startsWith(providerId + ":")
                        && (date == null || key.contains(":" + date.format(ISO_DATE) + ":")));
    }

    private String buildAvailabilityCacheKey(Long providerId, Long providerServiceId, LocalDate date,
            Integer durationMinutes) {
        return providerId + ":" + providerServiceId + ":" + date.format(ISO_DATE) + ":"
                + Optional.ofNullable(durationMinutes).map(String::valueOf).orElse("default");
    }

    private record AvailabilityCacheEntry(BookingAvailabilityResponse response, LocalDateTime expiresAtUtc) {
    }

    private ProviderBusinessHour resolveBusinessHour(Long providerId, LocalDate date) {
        ProviderBusinessHour hour = providerBusinessHourRepository.findByProvider_IdOrderByWeekdayAscIdAsc(providerId)
                .stream()
                .filter(item -> Objects.equals(item.getWeekday(), toPetGoWeekday(date)))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Shop chưa mở nhận booking vào ngày này"));
        if (Boolean.TRUE.equals(hour.getClosed()) || hour.getOpensAt() == null || hour.getClosesAt() == null) {
            throw new BadRequestException("Shop không nhận booking vào ngày này");
        }
        return hour;
    }

    private int resolveDuration(ProviderService service, Integer requestedDuration) {
        int fixed = Math.max(Optional.ofNullable(service.getDurationMinutes()).orElse(30), 5);
        return fixed;
    }

    private int resolveBufferAfter(ProviderService service) {
        return Math.max(Optional.ofNullable(service.getBookingBufferMinutes()).orElse(0), 0);
    }

    private ZoneId resolveZone(ProviderProfile provider) {
        try {
            return ZoneId.of(firstNonBlank(defaultTimezone, "Asia/Ho_Chi_Minh"));
        } catch (Exception ex) {
            return ZoneId.of("Asia/Ho_Chi_Minh");
        }
    }

    private void validateNotPast(ProviderProfile provider, LocalDate appointmentDate, LocalTime startTime) {
        if (appointmentDate == null || startTime == null) {
            throw new BadRequestException("Vui lòng chọn ngày và giờ hẹn hợp lệ");
        }
        ZoneId zone = resolveZone(provider);
        LocalDate today = LocalDate.now(zone);
        if (appointmentDate.isBefore(today)
                || (appointmentDate.isEqual(today) && !startTime.isAfter(LocalTime.now(zone)))) {
            throw new BadRequestException("Không thể đặt lịch trong quá khứ");
        }
    }

    private void validateServiceBelongsToProvider(ProviderProfile provider, ProviderService service) {
        if (service.getProvider() == null || !Objects.equals(service.getProvider().getId(), provider.getId())) {
            throw new BadRequestException("Dịch vụ không thuộc nhà cung cấp đã chọn");
        }
    }

    private boolean overlapsBreak(LocalTime start, LocalTime end, ProviderBusinessHour hour) {
        return hour.getBreakStartsAt() != null && hour.getBreakEndsAt() != null
                && start.isBefore(hour.getBreakEndsAt()) && end.isAfter(hour.getBreakStartsAt());
    }

    private int resolveMaxConcurrent(ProviderBusinessHour hour, List<ProviderScheduleException> exceptions,
            LocalTime slotStart) {
        int maxConcurrent = 1;
        if (exceptions == null || exceptions.isEmpty()) {
            return maxConcurrent;
        }
        for (ProviderScheduleException exception : exceptions) {
            String type = firstNonBlank(exception.getType(), "").toUpperCase(Locale.ROOT);
            if ("CLOSED".equals(type)) {
                return 0;
            }
            boolean applies = slotStart == null || exception.getStartsAtLocal() == null
                    || exception.getEndsAtLocal() == null
                    || (!slotStart.isBefore(exception.getStartsAtLocal())
                            && slotStart.isBefore(exception.getEndsAtLocal()));
            if (applies && ("CAPACITY_OVERRIDE".equals(type) || "OPEN_OVERRIDE".equals(type))
                    && exception.getMaxConcurrentOverride() != null) {
                maxConcurrent = Math.max(exception.getMaxConcurrentOverride(), 0);
            }
        }
        return maxConcurrent;
    }

    private boolean isBlockedByException(LocalTime start, LocalTime end, List<ProviderScheduleException> exceptions) {
        if (exceptions == null || exceptions.isEmpty()) {
            return false;
        }
        for (ProviderScheduleException exception : exceptions) {
            String type = firstNonBlank(exception.getType(), "").toUpperCase(Locale.ROOT);
            if ("CLOSED".equals(type)) {
                return true;
            }
            if ("PARTIAL_BLOCK".equals(type) && exception.getStartsAtLocal() != null
                    && exception.getEndsAtLocal() != null
                    && start.isBefore(exception.getEndsAtLocal()) && end.isAfter(exception.getStartsAtLocal())) {
                return true;
            }
        }
        return false;
    }

    private int toPetGoWeekday(LocalDate date) {
        return date.getDayOfWeek().getValue() == 7 ? 7 : date.getDayOfWeek().getValue();
    }

    private String firstNonBlank(String... values) {
        if (values == null)
            return null;
        for (String value : values) {
            if (value != null && !value.isBlank())
                return value.trim();
        }
        return null;
    }
}