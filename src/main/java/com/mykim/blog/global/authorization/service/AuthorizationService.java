package com.mykim.blog.global.authorization.service;

import com.mykim.blog.global.authorization.domain.AuthorizationSession;
import com.mykim.blog.global.authorization.dto.request.RequestAuthorizationDto;
import com.mykim.blog.global.authorization.repository.AuthorizationSessionRepository;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.exception.InvalidSignInInfoException;
import com.mykim.blog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.mykim.blog.global.error.ErrorCode.INVALID_SIGN_IN_INFO;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthorizationService {
    private final MemberRepository memberRepository;
    private final AuthorizationSessionRepository authorizationSessionRepository;

    @Transactional
    public String authenticate(RequestAuthorizationDto authorizationDto) {
        // id, password 검증
        Member member = memberRepository.findByEmailAndPassword(authorizationDto.getEmail(), authorizationDto.getPassword())
                                            .orElseThrow(() -> new InvalidSignInInfoException(INVALID_SIGN_IN_INFO));


        /**
         *  AuthorizationSession 검증
         */
        AuthorizationSession authorizationSession = authorizationSessionRepository.findByMemberId(member.getId())
                                                        .orElseGet(() -> createAuthorizationSession(member)); // 발급된 session이 없다면

        // 세션의 만료시간이 지났다면 : 기존 발급토큰 삭제 후 새로운 토큰 발급
        if(authorizationSession.getTokenExpirationTime().isBefore(LocalDateTime.now())) {
            // 기존 발급된 AuthorizationSession 비활성화 isActive => false;
            authorizationSession.deactivateAuthorizationSession();

            // 새 AuthorizationSession 발급
            return createAuthorizationSession(member).getAccessToken();
        }

        return authorizationSession.getAccessToken();
    }

    @Transactional
    public AuthorizationSession createAuthorizationSession(Member member) {
        AuthorizationSession authorizationSession = AuthorizationSession.builder()
                                                                            .member(member)
                                                                            .build();
        authorizationSessionRepository.save(authorizationSession);
        return authorizationSession;
    }

    @Transactional
    public void expireAuthorizationSession(String accessToken) {
        AuthorizationSession authorizationSession = authorizationSessionRepository.findByAccessToken(accessToken).orElse(null);
        if(authorizationSession != null) {
            authorizationSession.deactivateAuthorizationSession();
        }
    }




}
