package com.mykim.blog.auth.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import spring.security.jwt.global.result.CommonResult;
import spring.security.jwt.global.result.error.ErrorCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        log.error("sign-in error");

        // 해당계정이 없을때
        if (exception instanceof UsernameNotFoundException) {
            sendErrorResponse(response, ErrorCode.NOT_FOUND_USER);
        }

        // 비밀번호가 틀릴때 BadCredentialsException < AuthenticationException < RuntimeException
        if (exception instanceof BadCredentialsException) {
            sendErrorResponse(response, ErrorCode.PASSWORD_NOT_MATCH);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws HttpMessageNotWritableException, IOException {
        response.setStatus(errorCode.getStatus());

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        MediaType jsonMimeType = MediaType.APPLICATION_JSON;

        CommonResult result = CommonResult.createBusinessExceptionResult(errorCode);

        if(jsonConverter.canWrite(result.getClass(), jsonMimeType)) {
            jsonConverter.write(result, jsonMimeType, new ServletServerHttpResponse(response));
        }
    }
}
