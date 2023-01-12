package com.mykim.blog.global.error;

import com.mykim.blog.global.response.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mykim.blog.global.error.ErrorCode.VALIDATION_ERROR;

@Slf4j
@ControllerAdvice
// @RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * {
     *     status : 400,
     *     code : "V001",
     *     message : "validation error",
     *     data : [
     *          {
     *              fieldName : "",
     *              errorMessage : "",
     *          }
     *     ]
     * }
     */
    // 데이터 검증 Exception 처리
    //@ResponseStatus(HttpStatus.BAD_REQUEST) // 400, @RestControllerAdvice
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResult> validationExceptionHandler(MethodArgumentNotValidException e) {
        log.error("exception = {}" , e);

        List<ValidationError> validationErrors = new ArrayList<>();

        if(e.hasErrors()) {
            List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                String fieldName = fieldError.getField();
                String errorMessage = fieldError.getDefaultMessage();
                log.error("field name = {}", fieldName);
                log.error("error message = {}", errorMessage);

                validationErrors.add(ValidationError.builder()
                                                        .fieldName(fieldName)
                                                        .errorMessage(errorMessage)
                                                        .build());
            }
        }

        CommonResult commonResult = new CommonResult(VALIDATION_ERROR, validationErrors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(commonResult);
    }

    /**
     * @Service Exception : Business Rollback
     */
    @ExceptionHandler(BusinessRollbackException.class)
    public ResponseEntity<CommonResult> BusinessRollbackExceptionHandler(BusinessRollbackException e) {
        log.error(e.getErrorCode().getCode() + " : " + e.getErrorCode().getMessage());
        final ErrorCode errorCode = e.getErrorCode();

        CommonResult commonResult = new CommonResult(errorCode);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(commonResult);
    }



}
