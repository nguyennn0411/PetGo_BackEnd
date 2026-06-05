package com.example.petgo.service;

import tools.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class AiChatService {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;

    public AiChatService(
            WebClient.Builder webClientBuilder,
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model:gemini-2.5-flash}") String model
    ) {
        this.webClient = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        this.apiKey = apiKey;
        this.model = model;
    }

    public String sendMessage(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Anh vui lòng nhập nội dung cần hỏi nhé.";
        }

        try {
            String prompt = """
                    Bạn là chatbot chăm sóc khách hàng của PetGo.
                    PetGo là website dịch vụ thú cưng và cửa hàng sản phẩm thú cưng.
                    Hãy trả lời bằng tiếng Việt, thân thiện, ngắn gọn, dễ hiểu.
                    Nếu khách hỏi về đặt lịch, dịch vụ, thú cưng, đơn hàng, thanh toán, sản phẩm, hãy hướng dẫn rõ ràng.
                    Nếu không biết thông tin nội bộ cụ thể, hãy khuyên khách liên hệ nhân viên hỗ trợ.

                    Câu hỏi của khách:
                    """ + userMessage;

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "role", "user",
                                    "parts", List.of(
                                            Map.of("text", prompt)
                                    )
                            )
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.7,
                            "maxOutputTokens", 800
                    )
            );

            JsonNode response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/" + model + ":generateContent")
                            .queryParam("key", apiKey)
                            .build()
                    )
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response == null) {
                return "Xin lỗi, AI chưa trả về phản hồi.";
            }

            JsonNode textNode = response
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            if (textNode.isMissingNode() || textNode.asText().isBlank()) {
                return "Xin lỗi, AI chưa có câu trả lời phù hợp.";
            }

            return textNode.asText();

        } catch (WebClientResponseException e) {
            System.out.println("Gemini API Status: " + e.getStatusCode());
            System.out.println("Gemini API Body: " + e.getResponseBodyAsString());

            if (e.getStatusCode().value() == 400) {
                return "Lỗi cấu hình request gửi tới Gemini. Anh kiểm tra lại model hoặc nội dung request.";
            }

            if (e.getStatusCode().value() == 401 || e.getStatusCode().value() == 403) {
                return "Gemini API key không đúng, chưa bật quyền, hoặc project chưa được phép dùng API.";
            }

            if (e.getStatusCode().value() == 429) {
                return "Chatbot Gemini đang bị giới hạn lượt gọi. Anh thử lại sau hoặc kiểm tra quota Gemini API.";
            }

            return "Xin lỗi, hệ thống AI đang gặp lỗi. Mã lỗi: " + e.getStatusCode();

        } catch (Exception e) {
            e.printStackTrace();
            return "Xin lỗi, hệ thống chatbot đang gặp sự cố.";
        }
    }
}