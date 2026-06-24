package com.example.petgo.service;

import com.example.petgo.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface BookingService {

    CreateContextResponse getCreateContext(HttpServletRequest request, Long areaId);

    BookingCreateResponse createBooking(HttpServletRequest request, BookingCreateRequest req);

    List<BookingResponse> getMyBookings(HttpServletRequest request);

    BookingResponse getMyBookingDetail(HttpServletRequest request, Long id);

    BookingResponse cancelMyBooking(HttpServletRequest request, Long id);
}
