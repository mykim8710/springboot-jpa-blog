package com.mykim.blog.global.authorization.argumentresolver;

import com.mykim.blog.global.authorization.domain.AuthorizationSession;
import com.mykim.blog.global.authorization.dto.response.ResponseAuthorizationDto;
import com.mykim.blog.global.authorization.annotation.CustomAuthorization;
import com.mykim.blog.global.error.ErrorCode;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.exception.UnAuthorizedMemberException;
import com.mykim.blog.global.authorization.repository.AuthorizationSessionRepository;
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
    private final AuthorizationSessionRepository memberSessionRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasCustomAuthorizationAnnotation = parameter.hasParameterAnnotation(CustomAuthorization.class);
        boolean hasSignInMemberType = ResponseAuthorizationDto.class.isAssignableFrom(parameter.getParameterType());
        return hasCustomAuthorizationAnnotation && hasSignInMemberType;
    }

    // 인증 시 작동 @CustomAuthorization ResponseAuthorizationDto responseAuthorizationMemberDto 있는 Controller 메서드
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
         *  DB 확인 : AuthorizationSession 검증
         */
        AuthorizationSession authorizationSession = memberSessionRepository.findByAccessToken(authCookie.getValue())
                                            .orElseThrow(() -> new UnAuthorizedMemberException(ErrorCode.UNAUTHORIZED_MEMBER));

        // 토큰 만료일 검증
        if(authorizationSession.getTokenExpirationTime().isBefore(LocalDateTime.now()) || !authorizationSession.isActive()) {
            throw new UnAuthorizedMemberException(ErrorCode.UNAUTHORIZED_MEMBER);
        }

        Member member = authorizationSession.getMember();
        return ResponseAuthorizationDto.builder()
                                            .memberId(member.getId())
                                            .email(member.getEmail())
                                            .username(member.getUsername())
                                            .build();
    }
}
