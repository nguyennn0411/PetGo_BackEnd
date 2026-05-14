package com.example.petgo.service;

public interface MailService {
    void sendOtpEmail(String to, String otp);
    void sendPasswordResetOtpEmail(String to, String otp);
}
