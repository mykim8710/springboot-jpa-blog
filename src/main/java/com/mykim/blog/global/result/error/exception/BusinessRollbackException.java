package com.mykim.blog.global.result.error.exception;

import com.mykim.blog.global.result.error.ErrorCode;
import lombok.Getter;

@Getter
public abstract class BusinessRollbackException extends RuntimeException {
    private ErrorCode errorCode;

    public BusinessRollbackException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessRollbackException(Throwable e, ErrorCode errorCode) {
        super(e);
        this.errorCode = errorCode;
    }

    public BusinessRollbackException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
