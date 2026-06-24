package com.example.petgo.service;

import com.example.petgo.dto.BookingResponse;
import com.example.petgo.dto.BookingStatusUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AdminBookingService {

    List<BookingResponse> getAllBookings(Long areaId, String status, String date);

    BookingResponse getBookingDetail(Long id);

    BookingResponse confirmBooking(HttpServletRequest request, Long id, BookingStatusUpdateRequest req);

    BookingResponse completeBooking(HttpServletRequest request, Long id, BookingStatusUpdateRequest req);

    BookingResponse cancelBooking(HttpServletRequest request, Long id, BookingStatusUpdateRequest req);

    BookingResponse rejectBooking(HttpServletRequest request, Long id, BookingStatusUpdateRequest req);

    void updateBookingNote(Long id, BookingStatusUpdateRequest req);
}
