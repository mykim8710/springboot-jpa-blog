package com.mykim.blog.global.result.error;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ErrorCode {

    VALIDATION_ERROR(400, "V001", "validation error"),

    NOT_FOUND_POST(404, "P001", "not found this post"),

    DUPLICATE_USER_EMAIL(400, "M001", "This email is exist."),

    UNAUTHORIZED_MEMBER(401, "M002", "You need authorization."),

    INVALID_SIGN_IN_INFO(400, "M003", "email or password is not matched."),
    NOT_FOUND_MEMBER(404, "M004", "not found this member"),
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
