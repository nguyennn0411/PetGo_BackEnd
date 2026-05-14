package com.example.petgo.service.partner;

import com.example.petgo.dto.partner.PartnerScheduleResponse;
import com.example.petgo.dto.partner.PartnerWeeklyScheduleRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface PartnerScheduleService {
    PartnerScheduleResponse getSchedule(HttpServletRequest request, String from, String to);

    PartnerScheduleResponse updateWeeklySchedule(HttpServletRequest request, PartnerWeeklyScheduleRequest requestBody);
}