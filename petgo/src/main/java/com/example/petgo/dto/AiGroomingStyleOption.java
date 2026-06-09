package com.example.petgo.dto;

public class AiGroomingStyleOption {
    private String styleName;
    private String description;

    public AiGroomingStyleOption() {
    }

    public AiGroomingStyleOption(String styleName, String description) {
        this.styleName = styleName;
        this.description = description;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
