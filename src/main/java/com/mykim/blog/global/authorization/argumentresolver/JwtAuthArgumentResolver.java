package com.mykim.blog.global.authorization.argumentresolver;

import com.mykim.blog.global.authorization.annotation.CustomAuthorization;
import com.mykim.blog.global.authorization.dto.response.ResponseAuthorizationDto;
import com.mykim.blog.global.error.ErrorCode;
import com.mykim.blog.member.exception.UnAuthorizedMemberException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class JwtAuthArgumentResolver implements HandlerMethodArgumentResolver {
    private static final String KEY = "jiS48dIGRtD73A/st4gd8SxL7AHSdkPtNb7oO9p22rI=";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasCustomAuthorizationAnnotation = parameter.hasParameterAnnotation(CustomAuthorization.class);
        boolean hasSignInMemberType = ResponseAuthorizationDto.class.isAssignableFrom(parameter.getParameterType());
        return hasCustomAuthorizationAnnotation && hasSignInMemberType;
    }

    // 인증 시 작동 @CustomAuthorization ResponseAuthorizationDto responseAuthorizationMemberDto 있는 Controller 메서드
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String jwtToken = webRequest.getHeader("Authorization");
        System.out.println("jwtToken = " + jwtToken);

        if(!StringUtils.hasText(jwtToken)) {
            throw new UnAuthorizedMemberException(ErrorCode.UNAUTHORIZED_MEMBER);
        }

        byte[] decodedKey = Base64.decodeBase64(KEY);

        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                                            .setSigningKey(decodedKey)
                                            .build()
                                            .parseClaimsJws(jwtToken);

            log.info("claimsJws = {}", claimsJws);
            // OK, we can trust this JWT

        } catch (JwtException e) {
            //don't trust the JWT!
            throw new UnAuthorizedMemberException(ErrorCode.UNAUTHORIZED_MEMBER);
        }

        return null;
    }
}
