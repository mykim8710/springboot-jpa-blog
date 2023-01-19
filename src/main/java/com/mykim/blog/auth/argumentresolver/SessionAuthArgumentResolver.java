package com.mykim.blog.auth.argumentresolver;

import com.mykim.blog.auth.annotation.CustomSessionAuthorization;
import com.mykim.blog.auth.domain.AuthSession;
import com.mykim.blog.auth.dto.response.ResponseAuthDto;
import com.mykim.blog.global.error.ErrorCode;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.exception.UnAuthorizedMemberException;
import com.mykim.blog.auth.repository.AuthSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class SessionAuthArgumentResolver implements HandlerMethodArgumentResolver {
    private final AuthSessionRepository authSessionRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasCustomAuthorizationAnnotation = parameter.hasParameterAnnotation(CustomSessionAuthorization.class);
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

        Cookie authCookie = Arrays.stream(cookies)
                                         .filter(cookie -> cookie.getName().equals("AUTHORIZATION_SESSION"))
                                         .findFirst()
                                         .orElseThrow(()-> new UnAuthorizedMemberException(ErrorCode.UNAUTHORIZED_MEMBER));


        /**
         *  DB 확인 : AuthSession 검증
         */
        AuthSession authSession = authSessionRepository.findByAccessToken(authCookie.getValue())
                                            .orElseThrow(() -> new UnAuthorizedMemberException(ErrorCode.UNAUTHORIZED_MEMBER));

        // 토큰 만료일 검증
        if(authSession.getTokenExpirationTime().isBefore(LocalDateTime.now()) || !authSession.isActive()) {
            throw new UnAuthorizedMemberException(ErrorCode.UNAUTHORIZED_MEMBER);
        }

        Member member = authSession.getMember();
        return ResponseAuthDto.builder()
                                .memberId(member.getId())
                                .email(member.getEmail())
                                .username(member.getUsername())
                                .build();
    }
}
