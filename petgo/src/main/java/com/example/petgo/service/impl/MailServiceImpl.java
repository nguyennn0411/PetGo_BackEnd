package com.example.petgo.service.impl;

import com.example.petgo.entity.Booking;
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

    @Override
    public void sendPasswordResetOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Mã OTP đặt lại mật khẩu PetGo");
        message.setText("Chào bạn,\n\nMã OTP đặt lại mật khẩu PetGo của bạn là: " + otp
                + "\n\nMã này sẽ hết hạn sau 10 phút. Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\nCảm ơn bạn đã sử dụng PetGo!");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Lỗi khi gửi mail đặt lại mật khẩu tới {}: {}", to, e.getMessage());
        }
    }

    @Override
    public void sendBookingWorkflowEmail(String to, String eventType, Booking booking, String detail) {
        if (to == null || to.isBlank()) {
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("PetGo Booking - " + bookingSubject(eventType));
        message.setText(buildBookingEmailText(eventType, booking, detail));
        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Lỗi khi gửi mail booking {} tới {}: {}", eventType, to, e.getMessage());
        }
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
