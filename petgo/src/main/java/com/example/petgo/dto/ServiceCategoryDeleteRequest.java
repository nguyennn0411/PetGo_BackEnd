package com.example.petgo.dto;

public record ServiceCategoryDeleteRequest(
        Long moveServicesToCategoryId,
        Boolean hardDelete) {
}