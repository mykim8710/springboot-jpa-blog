package com.mykim.blog.auth.service;

import com.mykim.blog.auth.config.JwtConfig;
import com.mykim.blog.auth.dto.request.RequestAuthDto;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.exception.InvalidSignInInfoException;
import com.mykim.blog.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Calendar;
import java.util.Date;

import static com.mykim.blog.global.error.ErrorCode.INVALID_SIGN_IN_INFO;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtAuthService {
    private static final int JWT_TOKEN_VALIDATE_DAY=30;
    private final MemberRepository memberRepository;
    private final JwtConfig jwtConfig;

    @Transactional
    public String authenticateJwt(RequestAuthDto authorizationDto, String jwtToken) {
        // id, password 검증
        Member member = memberRepository.findByEmailAndPassword(authorizationDto.getEmail(), authorizationDto.getPassword())
                                            .orElseThrow(() -> new InvalidSignInInfoException(INVALID_SIGN_IN_INFO));

        /**
         * jwt token 검증
         */
        // jwtToken 없다면 새로 발급 후 리턴
        if(!StringUtils.hasText(jwtToken)) {
            return createJwt(member.getId());
        }

        // 토큰 만료여부 및 토큰데이터 확인
        try {
            byte[] decodedKey = jwtConfig.getDecodeKey();
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                                                .setSigningKey(decodedKey)
                                                .build()
                                                .parseClaimsJws(jwtToken);

            Long memberId = Long.parseLong(claimsJws.getBody().getSubject());
            if(!memberId.equals(member.getId())) {
                throw new JwtException("error, subject 값이 다릅니다.");
            }

            return jwtToken;
        } catch (JwtException e) {
            e.printStackTrace();
            log.error("this token is invalid");
            return createJwt(member.getId());
        }
    }

    /**
     * make SecretKey, Base64 encoding String
     *
     * Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
     * byte[] encodedKey = key.getEncoded();
     * String strKey = Base64.getEncoder().encodeToString(encodedKey);
     *
     *  jiS48dIGRtD73A/st4gd8SxL7AHSdkPtNb7oO9p22rI=
     */
    private String createJwt(Long memberId) {
        byte[] decodeKey = jwtConfig.getDecodeKey();
        SecretKey key = Keys.hmacShaKeyFor(decodeKey);
        return Jwts.builder()
                        .setSubject(String.valueOf(memberId))
                        .setExpiration(getJwtTokenExpireDate()) // 30일
                        .signWith(key)
                        .compact();
    }

    private Date getJwtTokenExpireDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, JWT_TOKEN_VALIDATE_DAY);
        return new Date(calendar.getTimeInMillis());
    }

}
