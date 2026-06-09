package com.example.petgo.service.partner;

import com.example.petgo.dto.partner.PartnerScheduleResponse;
import com.example.petgo.dto.partner.PartnerScheduleExceptionRequest;
import com.example.petgo.dto.partner.PartnerScheduleExceptionResponse;
import com.example.petgo.dto.partner.PartnerWeeklyScheduleRequest;
import com.example.petgo.dto.BookingLockRequest;
import com.example.petgo.dto.BookingLockResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface PartnerScheduleService {
    PartnerScheduleResponse getSchedule(HttpServletRequest request, String from, String to);

    PartnerScheduleResponse updateWeeklySchedule(HttpServletRequest request, PartnerWeeklyScheduleRequest requestBody);

    List<PartnerScheduleExceptionResponse> listDayOverrides(HttpServletRequest request, String from, String to);

    PartnerScheduleExceptionResponse getDayOverride(HttpServletRequest request, String date);

    PartnerScheduleExceptionResponse upsertDayOverride(HttpServletRequest request, String date,
            PartnerScheduleExceptionRequest requestBody);

    void deleteDayOverride(HttpServletRequest request, String date);

    List<PartnerScheduleExceptionResponse> copyDay(HttpServletRequest request, String sourceDate,
            List<String> targetDates);

    List<PartnerScheduleExceptionResponse> copyMonth(HttpServletRequest request, String sourceMonth,
            String targetMonth);

    List<PartnerScheduleExceptionResponse> copyYear(HttpServletRequest request, String sourceYear, String targetYear);

    List<BookingLockResponse> listBookingLocks(HttpServletRequest request, boolean activeOnly);

    BookingLockResponse createBookingLock(HttpServletRequest request, BookingLockRequest requestBody);

    BookingLockResponse extendBookingLock(HttpServletRequest request, Long lockId, Integer durationMinutes);

    BookingLockResponse unlockBookingLock(HttpServletRequest request, Long lockId);

    void deleteBookingLock(HttpServletRequest request, Long lockId);
}