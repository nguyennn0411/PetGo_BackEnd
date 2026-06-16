package com.example.petgo.service.impl;

import com.example.petgo.entity.Booking;
import com.example.petgo.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    private final String resendApiUrl = "https://api.resend.com/emails";

    private void sendEmailViaResend(String to, String subject, String text) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resendApiKey);

        Map<String, Object> body = new HashMap<>();
        // Đã đổi sang tên miền của bạn
        body.put("from", "PetGo <no-reply@petgo.website>");
        body.put("to", List.of(to));
        body.put("subject", subject);
        body.put("text", text);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(resendApiUrl, request, String.class);
            log.info("Gửi mail qua Resend thành công tới {}: {}", to, response.getBody());
        } catch (Exception e) {
            log.error("Lỗi khi gửi mail qua Resend tới {}: {}", to, e.getMessage());
        }
    }

    @Override
    public void sendOtpEmail(String to, String otp) {
        String subject = "Mã OTP xác thực tài khoản PetGo";
        String text = "Chào bạn,\n\nMã OTP của bạn là: " + otp + "\n\nMã này sẽ hết hạn sau 10 phút.\n\nCảm ơn bạn đã sử dụng PetGo!";
        
        sendEmailViaResend(to, subject, text);
    }

    @Override
    public void sendPasswordResetOtpEmail(String to, String otp) {
        String subject = "Mã OTP đặt lại mật khẩu PetGo";
        String text = "Chào bạn,\n\nMã OTP đặt lại mật khẩu PetGo của bạn là: " + otp
                + "\n\nMã này sẽ hết hạn sau 10 phút. Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\nCảm ơn bạn đã sử dụng PetGo!";

        sendEmailViaResend(to, subject, text);
    }

    @Override
    public void sendBookingWorkflowEmail(String to, String eventType, Booking booking, String detail) {
        if (to == null || to.isBlank()) {
            return;
        }
        String subject = "PetGo Booking - " + bookingSubject(eventType);
        String text = buildBookingEmailText(eventType, booking, detail);
        
        sendEmailViaResend(to, subject, text);
    }

    private String bookingSubject(String eventType) {
        return switch (String.valueOf(eventType).toUpperCase()) {
            case "CREATED" -> "Booking mới chờ xác nhận";
            case "CONFIRMED" -> "Booking đã được xác nhận";
            case "REJECTED" -> "Booking đã bị từ chối";
            case "DISPUTED" -> "Booking đang được khiếu nại";
            case "ADMIN_REVIEW" -> "Booking cần admin xem xét";
            case "RESOLVED" -> "Khiếu nại booking đã được xử lý";
            case "ESCROW_RELEASED" -> "Escrow booking đã được giải ngân";
            default -> "Cập nhật booking";
        };
    }

    private String buildBookingEmailText(String eventType, Booking booking, String detail) {
        String code = booking != null ? booking.getBookingCode() : "N/A";
        String service = booking != null ? booking.getServiceNameSnapshot() : "dịch vụ";
        String provider = booking != null ? booking.getProviderNameSnapshot() : "provider";
        String date = booking != null && booking.getAppointmentDate() != null ? booking.getAppointmentDate().toString() : "ngày đã chọn";
        String time = booking != null && booking.getStartTime() != null ? booking.getStartTime().toString() : "giờ đã chọn";
        return "Chào bạn,\n\n"
                + bookingSubject(eventType) + "\n\n"
                + "Mã booking: " + code + "\n"
                + "Dịch vụ: " + service + "\n"
                + "Provider: " + provider + "\n"
                + "Lịch hẹn: " + date + " " + time + "\n"
                + (detail != null && !detail.isBlank() ? "Ghi chú: " + detail + "\n" : "")
                + "\nVui lòng đăng nhập PetGo để xem chi tiết.\n\n"
                + "Cảm ơn bạn đã sử dụng PetGo!";
    }
}
