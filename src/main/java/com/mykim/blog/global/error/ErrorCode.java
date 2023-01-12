package com.mykim.blog.global.error;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ErrorCode {
    
    VALIDATION_ERROR(400, "V001", "validation error"),

    NOT_FOUND_POST(400, "P001", "not found this post")

    ;

    private int status;
    private String code;
    private String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
