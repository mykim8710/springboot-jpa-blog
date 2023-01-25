package com.mykim.blog.global.result.error;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ErrorCode {

    VALIDATION_ERROR(400, "V001", "validation error"),



    DUPLICATE_USER_EMAIL(400, "M001", "This email is exist."),


    INVALID_SIGN_IN_INFO(400, "M003", "email or password is not matched."),


    // security - auth(Authorization, Authentication)

    NOT_FOUND_MEMBER(404, "A001", "not found this member"),
    PASSWORD_NOT_MATCH(400, "A002", "password is not matched."),
    UNAUTHORIZED_MEMBER(401, "A003", "You need authorization."),
    ACCESS_DENIED(403, "A004", "Access denied."),



    // post
    NOT_FOUND_POST(404, "P001", "not found this post"),
    NOT_PERMIT_ACCESS_POST(400, "P002", "not permit access this post"),



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
