package com.example.petgo.entity;

public enum RoleType {
    USER("User", "Người dùng hệ thống"),
    PROVIDER("Provider", "Đối tác cung cấp dịch vụ"),
    ADMIN("Administrator", "Quản trị hệ thống");

    private final String displayName;
    private final String description;

    RoleType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getCode() {
        return name();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}