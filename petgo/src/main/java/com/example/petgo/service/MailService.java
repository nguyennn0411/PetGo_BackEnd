package com.example.petgo.service;

import com.example.petgo.entity.Booking;

public interface MailService {
    void sendOtpEmail(String to, String otp);

    void sendPasswordResetOtpEmail(String to, String otp);

    void sendBookingWorkflowEmail(String to, String eventType, Booking booking, String detail);
}
