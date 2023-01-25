package com.mykim.blog.auth.security;

import com.mykim.blog.auth.security.jwt.JwtProperties;
import com.mykim.blog.auth.security.jwt.JwtProvider;
import com.mykim.blog.auth.security.principal.PrincipalDetail;
import com.mykim.blog.global.result.CommonResult;
import com.mykim.blog.global.result.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;

    /**
     * JwtAuthenticationFilter -> CustomAuthenticationProvider(PrincipalDetailService, loadUserByUsername())를 통해 인증에 성공하면
     * 이 SuccessHandler로 이동
     *
     * CustomAuthenticationSuccessHandler에서는 토큰을 발급하고 유저에게 돌려준다.
     * JwtAuthenticationFilter에서 successfulAuthentication() 함수를 @Overrride하는것과 동일 => 역할을 구분하기위해 Handler사용
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("sign-in success");

        PrincipalDetail principalDetail = (PrincipalDetail) authentication.getPrincipal();
        String jwt = jwtProvider.createJwt(principalDetail.getMember().getId());
        log.info("jwt : {}", jwt);

        // set jwt token in header
        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX +jwt);


        response.setStatus(HttpStatus.OK.value());

        // set json type response
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        MediaType jsonMimeType = MediaType.APPLICATION_JSON;

        HttpStatus result = HttpStatus.OK;
        CommonResult commonResult = new CommonResult(SuccessCode.SIGN_IN);

        if (jsonConverter.canWrite(result.getClass(), jsonMimeType)) {
            jsonConverter.write(commonResult, jsonMimeType, new ServletServerHttpResponse(response));
        }
    }
}
