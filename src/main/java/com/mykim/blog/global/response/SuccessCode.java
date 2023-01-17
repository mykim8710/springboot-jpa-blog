package com.mykim.blog.global.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum SuccessCode {
    // Common
    COMMON(200, "", "OK"),

    SIGN_IN(200, "", "sign in success"),

    INSERT(200, "", "insert ok"),
    UPDATE(200, "", "update ok"),
    DELETE(200, "", "delete ok"),

    ;

    private int status;
    private String code;
    private String message;

    SuccessCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
