package com.example.petgo.service.impl;

import com.example.petgo.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Mã OTP xác thực tài khoản PetGo");
        message.setText("Chào bạn,\n\nMã OTP của bạn là: " + otp + "\n\nMã này sẽ hết hạn sau 10 phút.\n\nCảm ơn bạn đã sử dụng PetGo!");
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Lỗi khi gửi mail OTP tới {}: {}", to, e.getMessage());
            // Tuỳ chọn: ném ra ngoại lệ nếu muốn quá trình đăng ký dừng lại khi lỗi mail
            // throw new RuntimeException("Không thể gửi mail xác nhận.");
        }
    }
}
