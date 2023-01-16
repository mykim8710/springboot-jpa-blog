package com.mykim.blog.global.error.exception;

import com.mykim.blog.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class NotFoundException extends BusinessRollbackException {
    private final ErrorCode errorCode;
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
