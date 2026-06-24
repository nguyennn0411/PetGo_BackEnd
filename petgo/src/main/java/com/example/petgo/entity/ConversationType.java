package com.example.petgo.entity;

public enum ConversationType {
    REPORT("report", "Báo cáo lỗi"),
    QA("qa", "Hỏi đáp thắc mắc");

    private final String code;
    private final String label;

    ConversationType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
}
