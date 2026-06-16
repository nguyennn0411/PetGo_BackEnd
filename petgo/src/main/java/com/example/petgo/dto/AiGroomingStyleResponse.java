package com.example.petgo.dto;

public class AiGroomingStyleResponse {
    private String styleName;
    private String description;
    private String imageUrl;

    public AiGroomingStyleResponse() {
    }

    public AiGroomingStyleResponse(String styleName, String description, String imageUrl) {
        this.styleName = styleName;
        this.description = description;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
