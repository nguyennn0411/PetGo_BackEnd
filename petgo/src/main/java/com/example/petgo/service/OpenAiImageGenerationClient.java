package com.example.petgo.service;

import com.example.petgo.exception.AiProviderException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tools.jackson.databind.JsonNode;

import java.util.Map;

@Service
public class OpenAiImageGenerationClient {
    private final WebClient webClient;
    private final String apiKey;
    private final String imageModel;
    private final String imageSize;
    private final String imageQuality;

    public OpenAiImageGenerationClient(
            WebClient.Builder webClientBuilder,
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.image-model:dall-e-3}") String imageModel,
            @Value("${openai.image-size:1024x1024}") String imageSize,
            @Value("${openai.image-quality:standard}") String imageQuality
    ) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.openai.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
        this.apiKey = apiKey;
        this.imageModel = imageModel;
        this.imageSize = imageSize;
        this.imageQuality = imageQuality;
    }

    public String generateImageUrl(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new AiProviderException("Thiếu OPENAI_API_KEY trong biến môi trường.");
        }

        try {
            Map<String, Object> requestBody = Map.of(
                    "model", imageModel,
                    "prompt", prompt,
                    "n", 1,
                    "size", imageSize,
                    "quality", imageQuality,
                    "response_format", "url"
            );

            JsonNode response = webClient.post()
                    .uri("/v1/images/generations")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            String imageUrl = response == null ? null : response
                    .path("data")
                    .path(0)
                    .path("url")
                    .asText();

            if (imageUrl == null || imageUrl.isBlank()) {
                throw new AiProviderException("OpenAI không trả về imageUrl.");
            }

            return imageUrl;
        } catch (WebClientResponseException e) {
            String body = e.getResponseBodyAsString();
            throw new AiProviderException("OpenAI Image API lỗi " + e.getStatusCode().value() + ": " + body, e);
        } catch (AiProviderException e) {
            throw e;
        } catch (Exception e) {
            throw new AiProviderException("Không thể tạo ảnh minh họa kiểu lông bằng OpenAI.", e);
        }
    }
}
