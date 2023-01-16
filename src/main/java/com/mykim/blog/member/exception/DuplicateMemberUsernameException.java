package com.mykim.blog.member.exception;

import com.mykim.blog.global.error.ErrorCode;
import com.mykim.blog.global.error.exception.BusinessRollbackException;
import lombok.Getter;

@Getter
public class DuplicateMemberUsernameException extends BusinessRollbackException {
    private final ErrorCode errorCode;
    public DuplicateMemberUsernameException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
