package com.mykim.blog.auth.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {
    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] decodeKey = Base64.getDecoder().decode(JwtProperties.SECRET_KEY);
        this.key = Keys.hmacShaKeyFor(decodeKey);
    }

    // 토큰생성
    public String createJwt(Long memberId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(memberId));
        Date regDate = new Date();
        Date expirationDate = getJwtTokenExpireDate();

        return Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(regDate)
                        .setExpiration(expirationDate)
                        .signWith(key)
                        .compact();
    }

    private Date getJwtTokenExpireDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, JwtProperties.EXPIRATION_TIME);
        return new Date(calendar.getTimeInMillis());
    }


    // 토큰 만료일자 확인
    public boolean isValidTokenExpireDate(final String jwt){
        try{
            Jws<Claims> claims = getClaims(jwt.trim());
            return !claims.getBody().getExpiration().before(new Date());
        } catch(Exception e){
            return false;
        }
    }

    //Claims 추출
    public Jws<Claims> getClaims(final String jwt){
        return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build().parseClaimsJws(jwt);
    }

    // get Jwt token from Header
    public String resolveJwt(final HttpServletRequest request){
        String jwtHeader = request.getHeader(JwtProperties.HEADER_STRING);
        if (jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            return null;
        }

        return jwtHeader.replace(JwtProperties.TOKEN_PREFIX, "");
    }
}