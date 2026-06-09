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

    @Value("${app.bookings.minimum-lead-time-minutes:60}")
    private int minimumLeadTimeMinutes;

    @Override
    @Transactional(readOnly = true)
    public BookingAvailabilityResponse getAvailability(Long providerId, Long providerServiceId, LocalDate date,
            Integer durationMinutes) {
        int duration = 30;
        int buffer = 0;
        ProviderProfile provider = providerProfileRepository.findActiveById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp"));
        ProviderService service = providerServiceRepository.findActiveDetailById(providerServiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ"));
        validateServiceBelongsToProvider(provider, service);
        duration = resolveDuration(service, durationMinutes);
        buffer = resolveBufferAfter(service);
        ZoneId providerZone = resolveZone(provider);
        LocalDate today = LocalDate.now(providerZone);
        LocalDate resolvedDate = Optional.ofNullable(date).orElse(today);
        if (resolvedDate.isBefore(today)) {
            return BookingAvailabilityResponse.builder()
                    .providerId(provider.getId())
                    .providerServiceId(service.getId())
                    .date(resolvedDate.format(ISO_DATE))
                    .timezone(providerZone.getId())
                    .durationMinutes(duration)
                    .bufferAfterMinutes(buffer)
                    .maxConcurrent(0)
                    .status("PAST")
                    .reason("Ngày đã qua, vui lòng chọn ngày khác")
                    .slots(List.of())
                    .build();
        }
        String cacheKey = buildAvailabilityCacheKey(provider.getId(), service.getId(), resolvedDate, durationMinutes);
        AvailabilityCacheEntry cached = availabilityCache.get(cacheKey);
        if (cached != null && cached.expiresAtUtc().isAfter(LocalDateTime.now(UTC))) {
            return cached.response();
        }
        ProviderBusinessHour hour = resolveBusinessHour(provider.getId(), resolvedDate);
        List<ProviderScheduleException> exceptions = providerScheduleExceptionRepository
                .findByProvider_IdAndLocalDateOrderByStartsAtLocalAscIdAsc(provider.getId(), resolvedDate);
        int maxConcurrent = resolveMaxConcurrent(service, exceptions, null);

        List<BookingSlotOptionResponse> slots = new ArrayList<>();
        LocalTime cursor = hour.getOpensAt();
        while (!cursor.plusMinutes(duration).isAfter(hour.getClosesAt())) {
            LocalTime serviceEnd = cursor.plusMinutes(duration);
            LocalTime occupiedEnd = serviceEnd.plusMinutes(buffer);
            int slotMaxConcurrent = resolveMaxConcurrent(service, exceptions, cursor);
            if (slotMaxConcurrent > 0 && !overlapsBreak(cursor, occupiedEnd, hour)
                    && !isBlockedByException(cursor, occupiedEnd, exceptions)) {
                boolean lockedByProvider = bookingLockRepository.findActiveOverlappingLocks(provider.getId(),
                        service.getId(), resolvedDate, cursor, occupiedEnd, LocalDateTime.now(UTC)).stream()
                        .anyMatch(lock -> lock.getUser() != null && provider.getUser() != null
                                && Objects.equals(lock.getUser().getId(), provider.getUser().getId()));
                if (lockedByProvider) {
                    slots.add(BookingSlotOptionResponse.builder()
                            .slotId(null)
                            .providerServiceId(service.getId())
                            .serviceName(firstNonBlank(service.getCustomName(),
                                    service.getService() != null ? service.getService().getName() : null))
                            .date(resolvedDate.format(ISO_DATE))
                            .startTime(cursor.format(TIME_VIEW))
                            .endTime(serviceEnd.format(TIME_VIEW))
                            .label(cursor.format(TIME_VIEW))
                            .capacityRemaining(0)
                            .selected(false)
                            .status("LOCKED_BY_PROVIDER")
                            .reason("Provider đã khóa nhận booking trong khung giờ này")
                            .build());
                    cursor = cursor.plusMinutes(Math.max(slotStepMinutes, 5));
                    continue;
                }
                int occupied = countOccupied(provider.getId(), service.getId(), resolvedDate, cursor, occupiedEnd,
                        null);
                if (occupied < slotMaxConcurrent) {
                    boolean leadTimeOk = LocalDateTime.of(resolvedDate, cursor)
                            .isAfter(LocalDateTime.now(providerZone).plusMinutes(Math.max(minimumLeadTimeMinutes, 0)));
                    slots.add(BookingSlotOptionResponse.builder()
                            .slotId(null)
                            .providerServiceId(service.getId())
                            .serviceName(firstNonBlank(service.getCustomName(),
                                    service.getService() != null ? service.getService().getName() : null))
                            .date(resolvedDate.format(ISO_DATE))
                            .startTime(cursor.format(TIME_VIEW))
                            .endTime(serviceEnd.format(TIME_VIEW))
                            .label(cursor.format(TIME_VIEW))
                            .capacityRemaining(leadTimeOk ? slotMaxConcurrent - occupied : 0)
                            .selected(false)
                            .status(leadTimeOk ? "AVAILABLE" : "LEAD_TIME_REQUIRED")
                            .reason(leadTimeOk ? null
                                    : "Cần đặt trước tối thiểu " + Math.max(minimumLeadTimeMinutes, 0) + " phút")
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
                .status(slots.stream().anyMatch(slot -> "AVAILABLE".equalsIgnoreCase(slot.status())) ? "AVAILABLE"
                        : "FULL")
                .reason(slots.isEmpty() ? "Không có khung giờ khả dụng cho ngày này" : null)
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
        String lockType = firstNonBlank(request.lockType(), "LOCK_SLOT").toUpperCase(Locale.ROOT);
        LocalDate lockDate = Optional.ofNullable(request.appointmentDate())
                .orElse(LocalDate.now(resolveZone(provider)));
        LocalTime start = resolveLockStartTime(lockType, request.startTime());
        validateNotPast(provider, lockDate, start);
        ProviderBusinessHour hour = resolveBusinessHour(provider.getId(), lockDate);
        List<ProviderScheduleException> exceptions = providerScheduleExceptionRepository
                .findByProvider_IdAndLocalDateOrderByStartsAtLocalAscIdAsc(provider.getId(), lockDate);
        int duration = resolveLockDuration(lockType, service, request.durationMinutes(), request.endTime(), start);
        int buffer = resolveBufferAfter(service);
        LocalTime serviceEnd = start.plusMinutes(duration);
        LocalTime occupiedEnd = serviceEnd.plusMinutes(buffer);
        if (start.isBefore(hour.getOpensAt()) || serviceEnd.isAfter(hour.getClosesAt())
                || overlapsBreak(start, occupiedEnd, hour) || isBlockedByException(start, occupiedEnd, exceptions)) {
            throw new BadRequestException("Khung giờ đã chọn nằm ngoài lịch nhận booking của nhà cung cấp");
        }
        int maxConcurrent = resolveMaxConcurrent(service, exceptions, start);
        if (maxConcurrent <= 0) {
            throw new BadRequestException("Nhà cung cấp không nhận booking vào khung giờ này");
        }
        int occupied = countOccupied(provider.getId(), service.getId(), lockDate, start, occupiedEnd,
                owner.getId());
        if (occupied >= maxConcurrent) {
            log.warn(
                    "booking.slot_lock_rejected providerId={} serviceId={} date={} start={} end={} occupied={} maxConcurrent={}",
                    provider.getId(), service.getId(), lockDate, start, occupiedEnd, occupied,
                    maxConcurrent);
            throw new BadRequestException("Slot đã hết chỗ, vui lòng chọn giờ khác");
        }

        BookingLock lock = new BookingLock();
        lock.setProvider(provider);
        lock.setProviderService(service);
        lock.setUser(owner);
        lock.setAppointmentDate(lockDate);
        lock.setStartTime(start);
        lock.setEndTime(occupiedEnd);
        lock.setDurationMinutes(duration);
        lock.setBufferAfterMinutes(buffer);
        lock.setExpiresAtUtc(LocalDateTime.now(UTC).plusMinutes(Math.max(lockMinutes, 1)));
        lock.setStatus("ACTIVE");
        BookingLock saved = bookingLockRepository.save(lock);
        invalidateAvailabilityCache(provider.getId(), service.getId(), lockDate);

        return BookingLockResponse.builder()
                .lockId(saved.getId())
                .ownerUserId(owner.getId())
                .providerId(provider.getId())
                .providerServiceId(service.getId())
                .appointmentDate(lockDate.format(ISO_DATE))
                .startTime(start.format(TIME_VIEW))
                .endTime(serviceEnd.format(TIME_VIEW))
                .expiresAtUtc(saved.getExpiresAtUtc().toString())
                .expiresInSeconds(Math.max(lockMinutes, 1) * 60)
                .status(saved.getStatus())
                .message("Đã giữ slot trong " + Math.max(lockMinutes, 1) + " phút")
                .build();
    }

    private LocalTime resolveLockStartTime(String lockType, LocalTime requestedStart) {
        if ("LOCK_FROM_NOW_UNTIL_MANUAL_UNLOCK".equals(lockType) || "LOCK_DURATION_FROM_NOW".equals(lockType)) {
            return Optional.ofNullable(requestedStart).orElse(LocalTime.now().withSecond(0).withNano(0));
        }
        if (requestedStart == null) {
            throw new BadRequestException("Vui lòng chọn giờ hẹn hợp lệ cho loại lock này");
        }
        return requestedStart;
    }

    private int resolveLockDuration(String lockType, ProviderService service, Integer requestedDuration,
            LocalTime requestedEnd, LocalTime start) {
        if ("LOCK_TIME_RANGE".equals(lockType)) {
            if (requestedEnd == null || !start.isBefore(requestedEnd)) {
                throw new BadRequestException("LOCK_TIME_RANGE cần endTime sau startTime.");
            }
            return Math.max((int) java.time.Duration.between(start, requestedEnd).toMinutes(), 5);
        }
        if ("LOCK_DURATION_FROM_NOW".equals(lockType) || "LOCK_FROM_NOW_UNTIL_MANUAL_UNLOCK".equals(lockType)) {
            return Math.max(Optional.ofNullable(requestedDuration).orElse(resolveDuration(service, null)), 5);
        }
        return resolveDuration(service, requestedDuration);
    }

    private int countOccupied(Long providerId, Long serviceId, LocalDate date, LocalTime start, LocalTime end,
            Long lockOwnerUserId) {
        long bookingCount = bookingRepository.countActiveOverlappingBookings(providerId, serviceId, date, start, end);
        long lockCount = bookingLockRepository.countActiveOverlappingLocks(providerId, serviceId, date, start, end,
                LocalDateTime.now(UTC), lockOwnerUserId);
        long total = bookingCount + lockCount;
        return total > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) total;
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
                .orElseThrow(() -> new BadRequestException("Nhà cung cấp chưa mở nhận booking vào ngày này"));
        if (Boolean.TRUE.equals(hour.getClosed()) || hour.getOpensAt() == null || hour.getClosesAt() == null) {
            throw new BadRequestException("Nhà cung cấp không nhận booking vào ngày này");
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

    private int resolveMaxConcurrent(ProviderService service, List<ProviderScheduleException> exceptions,
            LocalTime slotStart) {
        int maxConcurrent = Math.max(Optional.ofNullable(service.getCapacityPerSlot()).orElse(1), 1);
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