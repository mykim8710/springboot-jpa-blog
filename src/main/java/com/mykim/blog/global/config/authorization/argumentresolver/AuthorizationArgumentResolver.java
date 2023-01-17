package com.mykim.blog.global.config.authorization.argumentresolver;

import com.mykim.blog.global.config.authorization.AuthorizationMember;
import com.mykim.blog.global.config.authorization.annotation.CustomAuthorization;
import com.mykim.blog.global.error.ErrorCode;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.domain.MemberSession;
import com.mykim.blog.member.exception.UnAuthorizedMemberException;
import com.mykim.blog.member.repository.MemberSessionRepository;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class AuthorizationArgumentResolver implements HandlerMethodArgumentResolver {
    private final MemberSessionRepository memberSessionRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasCustomAuthorizationAnnotation = parameter.hasParameterAnnotation(CustomAuthorization.class);
        boolean hasSignInMemberType = AuthorizationMember.class.isAssignableFrom(parameter.getParameterType());
        return hasCustomAuthorizationAnnotation && hasSignInMemberType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
        if(request.getCookies() == null) {
            throw new UnAuthorizedMemberException(ErrorCode.UNAUTHORIZED_MEMBER); // 로그인 재시도
        }

        Cookie accessTokenCookie = Arrays.stream(request.getCookies())
                                         .filter(cookie -> cookie.getName().equals("ACCESS_TOKEN"))
                                         .findFirst()
                                         .orElseThrow(()-> new UnAuthorizedMemberException(ErrorCode.UNAUTHORIZED_MEMBER));

        String accessTokenValue = accessTokenCookie.getValue();

        /**
         *  DB 사용자 확인작업
         */
        MemberSession memberSession = memberSessionRepository.findByAccessToken(accessTokenValue)
                                            .orElseThrow(() -> new UnAuthorizedMemberException(ErrorCode.UNAUTHORIZED_MEMBER));

        // 토큰 만료일 검증
        if(memberSession.getTokenExpirationTime().isBefore(LocalDateTime.now())) {
            throw new UnAuthorizedMemberException(ErrorCode.UNAUTHORIZED_MEMBER);
        }

        Member member = memberSession.getMember();

        return AuthorizationMember.builder()
                                .memberId(member.getId())
                                .email(member.getEmail())
                                .username(member.getUsername())
                                .build();
    }
}
