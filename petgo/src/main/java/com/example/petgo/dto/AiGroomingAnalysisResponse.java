package com.example.petgo.dto;

import java.util.ArrayList;
import java.util.List;

public class AiGroomingAnalysisResponse {
    private String petType;
    private String breed;
    private String color;
    private List<AiGroomingStyleOption> styles = new ArrayList<>();

    public AiGroomingAnalysisResponse() {
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<AiGroomingStyleOption> getStyles() {
        return styles;
    }

    public void setStyles(List<AiGroomingStyleOption> styles) {
        this.styles = styles;
    }
}
