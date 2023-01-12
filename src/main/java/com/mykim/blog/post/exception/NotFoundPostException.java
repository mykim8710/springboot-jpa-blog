package com.mykim.blog.post.exception;

import com.mykim.blog.global.error.BusinessRollbackException;
import com.mykim.blog.global.error.ErrorCode;

import static com.mykim.blog.global.error.ErrorCode.NOT_FOUND_POST;

public class NotFoundPostException extends BusinessRollbackException {

    public NotFoundPostException() {
        super(NOT_FOUND_POST);
    }
}
