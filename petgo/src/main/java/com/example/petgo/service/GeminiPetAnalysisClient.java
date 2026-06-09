package com.example.petgo.service;

import com.example.petgo.dto.AiGroomingAnalysisResponse;
import com.example.petgo.exception.AiProviderException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class GeminiPetAnalysisClient {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public GeminiPetAnalysisClient(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model:gemini-1.5-flash}") String model
    ) {
        this.webClient = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
    }

    public AiGroomingAnalysisResponse analyzePet(MultipartFile image) {
        try {
            String mimeType = image.getContentType() == null ? MediaType.IMAGE_JPEG_VALUE : image.getContentType();
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "role", "user",
                                    "parts", List.of(
                                            Map.of("text", buildAnalysisPrompt()),
                                            Map.of("inline_data", Map.of(
                                                    "mime_type", mimeType,
                                                    "data", base64Image
                                            ))
                                    )
                            )
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.2,
                            "maxOutputTokens", 1200,
                            "responseMimeType", "application/json"
                    )
            );

            JsonNode response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/" + model + ":generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            String jsonText = response == null ? null : response
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();

            if (jsonText == null || jsonText.isBlank()) {
                throw new AiProviderException("Gemini không trả về dữ liệu phân tích thú cưng.");
            }

            AiGroomingAnalysisResponse analysis = objectMapper.readValue(cleanJson(jsonText), AiGroomingAnalysisResponse.class);
            normalizeAnalysis(analysis);
            return analysis;
        } catch (WebClientResponseException e) {
            String body = e.getResponseBodyAsString();
            throw new AiProviderException("Gemini API lỗi " + e.getStatusCode().value() + ": " + body, e);
        } catch (IOException e) {
            throw new AiProviderException("Không đọc được ảnh thú cưng. Anh kiểm tra lại file ảnh nhé.", e);
        } catch (AiProviderException e) {
            throw e;
        } catch (Exception e) {
            throw new AiProviderException("Không thể phân tích ảnh thú cưng bằng Gemini.", e);
        }
    }

    private String buildAnalysisPrompt() {
        return """
                Bạn là chuyên gia grooming thú cưng của PetGo.
                Hãy phân tích ảnh thú cưng và trả về DUY NHẤT JSON hợp lệ, không markdown, không giải thích ngoài JSON.

                Yêu cầu:
                - Nhận diện loại thú cưng: dog/cat/unknown.
                - Dự đoán giống thú cưng nếu có thể. Nếu không chắc, dùng mô tả chung như Mixed Breed Dog hoặc Domestic Cat.
                - Mô tả màu lông ngắn gọn bằng tiếng Anh để dùng tạo ảnh AI.
                - Gợi ý chính xác 3 kiểu lông phù hợp, an toàn, dễ mang đi spa/grooming.
                - description viết tiếng Việt, thân thiện, tối đa 2 câu.

                JSON schema bắt buộc:
                {
                  "petType": "dog",
                  "breed": "Poodle",
                  "color": "brown curly coat",
                  "styles": [
                    {
                      "styleName": "Teddy Bear Cut",
                      "description": "Mô tả tiếng Việt..."
                    }
                  ]
                }
                """;
    }

    private String cleanJson(String raw) {
        String text = raw.trim();
        if (text.startsWith("```")) {
            text = text.replaceFirst("^```json", "")
                    .replaceFirst("^```", "")
                    .replaceFirst("```$", "")
                    .trim();
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }

    private void normalizeAnalysis(AiGroomingAnalysisResponse analysis) {
        if (analysis.getPetType() == null || analysis.getPetType().isBlank()) {
            analysis.setPetType("pet");
        }
        if (analysis.getBreed() == null || analysis.getBreed().isBlank()) {
            analysis.setBreed("cute pet");
        }
        if (analysis.getColor() == null || analysis.getColor().isBlank()) {
            analysis.setColor("natural coat");
        }
        if (analysis.getStyles() == null || analysis.getStyles().isEmpty()) {
            throw new AiProviderException("Gemini không trả về danh sách kiểu lông phù hợp.");
        }
    }
}
