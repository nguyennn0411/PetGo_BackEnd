package com.example.petgo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final Pattern MONEY_PATTERN = Pattern.compile("([0-9.,]+)\\s*đ");

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", ex.getMessage());
        body.put("status", 404);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", ex.getMessage());
        body.put("status", 400);
        if (ex instanceof InsufficientWalletBalanceException walletEx) {
            body.put("code", "INSUFFICIENT_WALLET_BALANCE");
            body.put("reason", "WALLET_BALANCE_NOT_ENOUGH");
            body.put("requiredAmount", walletEx.getRequiredAmount());
            body.put("currentBalance", walletEx.getCurrentBalance());
            body.put("missingAmount", walletEx.getMissingAmount());
            body.put("topUpUrl", walletEx.getTopUpUrl());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }
        enrichBookingError(body, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private void enrichBookingError(Map<String, Object> body, String message) {
        if (message == null)
            return;
        if (message.startsWith("INSUFFICIENT_WALLET_BALANCE:")) {
            body.put("code", "INSUFFICIENT_WALLET_BALANCE");
            body.put("reason", "WALLET_BALANCE_NOT_ENOUGH");
            body.put("topUpUrl", "/wallet");
            Matcher matcher = MONEY_PATTERN.matcher(message);
            if (matcher.find()) {
                try {
                    body.put("missingAmount", new BigDecimal(matcher.group(1).replace(".", "").replace(",", "")));
                } catch (NumberFormatException ignored) {
                }
            }
        } else if (message.contains("Khung giờ") || message.contains("Slot") || message.contains("lịch nhận booking")
                || message.contains("ngày này")) {
            body.put("code", "BOOKING_AVAILABILITY_UNAVAILABLE");
            body.put("reason", message);
        }
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        return build(HttpStatus.BAD_REQUEST, "Ảnh không được vượt quá 5MB.");
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Map<String, Object>> handleMissingRequestPart(MissingServletRequestPartException ex) {
        return build(HttpStatus.BAD_REQUEST, "Vui lòng chọn ảnh để upload.");
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String, Object>> handleMultipart(MultipartException ex) {
        return build(HttpStatus.BAD_REQUEST, "Upload file không hợp lệ. Vui lòng chọn lại ảnh.");
    }

    // @ExceptionHandler(ResourceNotFoundException.class)
    // public ResponseEntity<Map<String, Object>>
    // handleNotFound(ResourceNotFoundException ex) {
    // return build(HttpStatus.NOT_FOUND, ex.getMessage());
    // }
    //
    // @ExceptionHandler(BadRequestException.class)
    // public ResponseEntity<Map<String, Object>>
    // handleBadRequest(BadRequestException ex) {
    // return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    // }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        return build(HttpStatus.valueOf(ex.getStatusCode().value()),
                ex.getReason() != null ? ex.getReason() : "Yêu cầu không hợp lệ");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Dữ liệu không hợp lệ");
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccess(DataAccessException ex) {
        log.error("Database error while handling request", ex);
        if (ex instanceof InvalidDataAccessApiUsageException
                && ex.getMessage() != null
                && ex.getMessage().contains("already registered a copy")) {
            return build(HttpStatus.BAD_REQUEST,
                    "Dữ liệu lịch của khu vực đang bị trùng/cấu hình chưa hợp lệ, vui lòng kiểm tra lịch và dịch vụ.");
        }
        Throwable root = ex.getMostSpecificCause();
        String message = root != null && root.getMessage() != null ? root.getMessage() : ex.getMessage();
        return build(HttpStatus.INTERNAL_SERVER_ERROR, message != null ? message : "Lỗi truy cập dữ liệu");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unhandled error while handling request", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi nội bộ");
    }

    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
    // return build(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi nội bộ");
    // }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
