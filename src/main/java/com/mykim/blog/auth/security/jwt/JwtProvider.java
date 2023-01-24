package com.mykim.blog.auth.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {
    // 토큰생성
    public String createJwt(Long userId) {
        return JWT.create()
                .withIssuer("issuer")            // payload - issuer : 토큰 발급자
                .withSubject("userAuthJwtToken") // payload - subject : 토큰 제목
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME)) //  payload - expiration : 토큰 만료 시간
                // 비공개 클래임
                .withClaim("id", userId)
                .sign(Algorithm.HMAC512(JwtProperties.SECRET_KEY));
    }

    // 토큰 만료일자 확인
    public boolean isValidTokenExpireDate(final String jwt){
        Date expiresAt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET_KEY))
                            .build().verify(jwt).getExpiresAt();
        log.info("jwt token expire date : {}", expiresAt);
        return !expiresAt.before(new Date());
    }

    // get Jwt token from Header
    public String resolveJwt(final HttpServletRequest request){
        String jwtHeader = request.getHeader(JwtProperties.HEADER_STRING);
        if (jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            return null;
        }

        return jwtHeader.replace(JwtProperties.TOKEN_PREFIX, "");
    }}
