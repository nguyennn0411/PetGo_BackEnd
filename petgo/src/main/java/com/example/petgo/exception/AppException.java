package com.example.petgo.exception;


import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String fieldName;

    public AppException(ErrorCode errorCode) {
        this(errorCode, null);
    }

    public AppException(ErrorCode errorCode, String fieldName) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.fieldName = fieldName;
    }
}

