package com.mykim.blog.auth.argumentresolver;

import com.mykim.blog.auth.annotation.CustomJwtAuthorization;
import com.mykim.blog.auth.config.JwtConfig;
import com.mykim.blog.auth.dto.response.ResponseAuthDto;
import com.mykim.blog.global.error.ErrorCode;
import com.mykim.blog.global.error.exception.NotFoundException;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.exception.UnAuthorizedMemberException;
import com.mykim.blog.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static com.mykim.blog.global.error.ErrorCode.NOT_FOUND_MEMBER;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthArgumentResolver implements HandlerMethodArgumentResolver {
    private final MemberRepository memberRepository;
    private final JwtConfig jwtConfig;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasCustomAuthorizationAnnotation = parameter.hasParameterAnnotation(CustomJwtAuthorization.class);
        boolean hasSignInMemberType = ResponseAuthDto.class.isAssignableFrom(parameter.getParameterType());
        return hasCustomAuthorizationAnnotation && hasSignInMemberType;
    }

    // 인증 시 작동 @CustomSessionAuthorization ResponseAuthDto responseAuthorizationMemberDto 있는 Controller 메서드
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
        if(request == null) {
            log.error("HttpServletRequest is null.");
            throw new UnAuthorizedMemberException(ErrorCode.UNAUTHORIZED_MEMBER);
        }

        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length == 0) {
            log.error("Authorization cookie not exist.");
            throw new UnAuthorizedMemberException(ErrorCode.UNAUTHORIZED_MEMBER);
        }

        Cookie jwtCookie = Arrays.stream(cookies)
                                                .filter(cookie -> cookie.getName().equals("AUTHORIZATION_JWT"))
                                                .findFirst()
                                                .orElseThrow(()-> new UnAuthorizedMemberException(ErrorCode.UNAUTHORIZED_MEMBER));

        String jwtToken = jwtCookie.getValue();
        if(!StringUtils.hasText(jwtToken)) {
            throw new UnAuthorizedMemberException(ErrorCode.UNAUTHORIZED_MEMBER);
        }

        try {
            // OK, we can trust this JWT
            byte[] decodeKey = jwtConfig.getDecodeKey();
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                                                .setSigningKey(decodeKey)
                                                .build()
                                                .parseClaimsJws(jwtToken);

            Long memberId = Long.parseLong(claimsJws.getBody().getSubject());
            Member member = memberRepository.findById(memberId)
                                                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER));


            return ResponseAuthDto.builder()
                                    .memberId(member.getId())
                                    .email(member.getEmail())
                                    .username(member.getUsername())
                                    .build();
        } catch (JwtException e) {
            // don't trust the JWT!(expire time)
            log.error("this token is invalid");
            throw new UnAuthorizedMemberException(ErrorCode.UNAUTHORIZED_MEMBER);
        }
    }
}
