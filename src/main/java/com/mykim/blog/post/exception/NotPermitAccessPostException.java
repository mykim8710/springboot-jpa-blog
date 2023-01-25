package com.mykim.blog.post.exception;

import com.mykim.blog.global.result.error.ErrorCode;
import com.mykim.blog.global.result.error.exception.BusinessRollbackException;
import lombok.Getter;

@Getter
public class NotPermitAccessPostException extends BusinessRollbackException {
    private final ErrorCode errorCode;
    public NotPermitAccessPostException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
