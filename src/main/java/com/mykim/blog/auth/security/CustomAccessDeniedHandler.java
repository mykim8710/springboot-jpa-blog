package com.mykim.blog.auth.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import spring.security.jwt.global.result.CommonResult;
import spring.security.jwt.global.result.error.ErrorCode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private static final String REDIRECTION_URL = "/";
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("Access Denied, {}", accessDeniedException);
        log.info("URL : {}", request.getRequestURI());

        response.setStatus(HttpStatus.FORBIDDEN.value());

        if(request.getRequestURI().contains("api")) {
            MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
            MediaType jsonMimeType = MediaType.APPLICATION_JSON;
            CommonResult result = CommonResult.createBusinessExceptionResult(ErrorCode.ACCESS_DENIED);
            if(jsonConverter.canWrite(result.getClass(), jsonMimeType)) {
                jsonConverter.write(result, jsonMimeType, new ServletServerHttpResponse(response));
            }
        } else {
            response.sendRedirect(REDIRECTION_URL);
        }

    }
}
