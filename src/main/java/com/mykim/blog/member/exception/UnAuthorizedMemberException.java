package com.mykim.blog.member.exception;

import com.mykim.blog.global.result.error.ErrorCode;
import com.mykim.blog.global.result.error.exception.BusinessRollbackException;
import lombok.Getter;

@Getter
public class UnAuthorizedMemberException extends BusinessRollbackException {
    private final ErrorCode errorCode;
    public UnAuthorizedMemberException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
