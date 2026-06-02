package com.example.petgo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import vn.payos.PayOS;

@Configuration
public class PayOSConfig {

    @Value("${payos.client-id}")
    private String clientId;

    @Value("${payos.api-key}")
    private String apiKey;

    @Value("${payos.checksum-key}")
    private String checksumKey;

    @Bean
    public PayOS payOS() {
        // BẮT BUỘC ĐÚNG THỨ TỰ: clientId trước, rồi đến apiKey, cuối cùng là checksumKey
        // Để an toàn, hãy thêm log kiểm tra độ dài chuỗi ký tự lúc start-up
        System.out.println("====== KHỞI TẠO PAYOS BEAN ======");
        System.out.println("Client ID length: " + (clientId != null ? clientId.length() : "NULL"));
        System.out.println("API Key length: " + (apiKey != null ? apiKey.length() : "NULL"));
        System.out.println("Checksum Key length: " + (checksumKey != null ? checksumKey.length() : "NULL"));

        return new PayOS(clientId, apiKey, checksumKey);
    }
}