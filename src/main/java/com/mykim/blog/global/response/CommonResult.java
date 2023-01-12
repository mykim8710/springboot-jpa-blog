package com.mykim.blog.global.response;

import com.mykim.blog.global.error.ErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommonResult<T> {
    private int status;     // http Status(2XX, 3XX, 4XX....)
    private String code; 	// 지정 code
    private String message; // 메세지
    private T data;

    public CommonResult(SuccessCode successCode) {
        this.status = successCode.getStatus();
        this.code = successCode.getCode();
        this.message = successCode.getMessage();
    }

    public CommonResult(SuccessCode successCode, T data) {
        this.status = successCode.getStatus();
        this.code = successCode.getCode();
        this.message = successCode.getMessage();
        this.data = data;
    }

    public CommonResult(ErrorCode errorCode, T data) {
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.data = data;
    }
    public CommonResult(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }


}