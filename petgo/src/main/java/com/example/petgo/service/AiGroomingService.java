package com.example.petgo.service;

import com.example.petgo.dto.AiGroomingStyleResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AiGroomingService {
    List<AiGroomingStyleResponse> suggestStyles(MultipartFile image);
}
