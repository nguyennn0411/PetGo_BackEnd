package com.example.petgo.service.impl;

import com.example.petgo.dto.AiGroomingAnalysisResponse;
import com.example.petgo.dto.AiGroomingStyleOption;
import com.example.petgo.dto.AiGroomingStyleResponse;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.service.AiGroomingService;
import com.example.petgo.service.GeminiPetAnalysisClient;
import com.example.petgo.service.OpenAiImageGenerationClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class AiGroomingServiceImpl implements AiGroomingService {
    private final GeminiPetAnalysisClient geminiPetAnalysisClient;
    private final OpenAiImageGenerationClient openAiImageGenerationClient;
    private final Executor aiGroomingExecutor;

    public AiGroomingServiceImpl(
            GeminiPetAnalysisClient geminiPetAnalysisClient,
            OpenAiImageGenerationClient openAiImageGenerationClient,
            @Qualifier("aiGroomingExecutor") Executor aiGroomingExecutor
    ) {
        this.geminiPetAnalysisClient = geminiPetAnalysisClient;
        this.openAiImageGenerationClient = openAiImageGenerationClient;
        this.aiGroomingExecutor = aiGroomingExecutor;
    }

    @Override
    public List<AiGroomingStyleResponse> suggestStyles(MultipartFile image) {
        validateImage(image);

        AiGroomingAnalysisResponse analysis = geminiPetAnalysisClient.analyzePet(image);

        List<AiGroomingStyleOption> styles = analysis.getStyles()
                .stream()
                .filter(Objects::nonNull)
                .filter(style -> style.getStyleName() != null && !style.getStyleName().isBlank())
                .limit(3)
                .toList();

        if (styles.isEmpty()) {
            throw new BadRequestException("AI chưa gợi ý được kiểu lông phù hợp cho ảnh này.");
        }

        List<CompletableFuture<AiGroomingStyleResponse>> futures = styles.stream()
                .map(style -> CompletableFuture.supplyAsync(
                        () -> generateStyleCard(analysis, style),
                        aiGroomingExecutor
                ).exceptionally(ex -> new AiGroomingStyleResponse(
                        style.getStyleName(),
                        style.getDescription(),
                        null
                )))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    private AiGroomingStyleResponse generateStyleCard(AiGroomingAnalysisResponse analysis, AiGroomingStyleOption style) {
        String prompt = buildImagePrompt(analysis, style.getStyleName());
        String imageUrl = openAiImageGenerationClient.generateImageUrl(prompt);
        return new AiGroomingStyleResponse(style.getStyleName(), style.getDescription(), imageUrl);
    }

    private String buildImagePrompt(AiGroomingAnalysisResponse analysis, String styleName) {
        String petType = safe(analysis.getPetType(), "pet");
        String breed = safe(analysis.getBreed(), "cute pet");
        String color = safe(analysis.getColor(), "natural coat");

        return "A realistic professional pet grooming photo of a cute "
                + color + " " + breed + " " + petType
                + " with " + styleName + " haircut style, clean trimmed fur, happy expression, "
                + "studio pet photography, soft photostudio lighting, neutral pastel background, high detail, "
                + "full body centered composition, no human, no text, no watermark, safe and natural grooming result.";
    }

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Anh vui lòng tải lên ảnh thú cưng.");
        }
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("File tải lên phải là ảnh JPG, PNG hoặc WEBP.");
        }
    }
}
