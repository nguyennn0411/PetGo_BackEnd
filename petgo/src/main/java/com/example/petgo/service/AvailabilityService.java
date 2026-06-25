package com.example.petgo.service;

import com.example.petgo.dto.AvailabilityDateResponse;
import com.example.petgo.dto.AvailabilitySlotResponse;

import java.util.List;

public interface AvailabilityService {

    List<AvailabilityDateResponse> getAvailableDates(Long areaId, Long serviceId, String from, Integer days);

    List<AvailabilitySlotResponse> getAvailableSlots(Long areaId, Long serviceId, String date);
}
