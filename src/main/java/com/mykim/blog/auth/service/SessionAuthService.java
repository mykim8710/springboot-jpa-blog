package com.mykim.blog.auth.service;

import com.mykim.blog.auth.domain.AuthSession;
import com.mykim.blog.auth.dto.request.RequestAuthDto;
import com.mykim.blog.auth.repository.AuthSessionRepository;
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
public class SessionAuthService {
    private final MemberRepository memberRepository;
    private final AuthSessionRepository authSessionRepository;

    @Transactional
    public String authenticate(RequestAuthDto authDto) {
        // id, password 검증
        Member member = memberRepository.findByEmailAndPassword(authDto.getEmail(), authDto.getPassword())
                                            .orElseThrow(() -> new InvalidSignInInfoException(INVALID_SIGN_IN_INFO));


        /**
         *  AuthSession 검증
         */
        AuthSession authSession = authSessionRepository.findByMemberId(member.getId())
                                                        .orElseGet(() -> createAuthSession(member)); // 발급된 session이 없다면

        // 세션의 만료시간이 지났다면 : 기존 발급토큰 삭제 후 새로운 토큰 발급
        if(authSession.getTokenExpirationTime().isBefore(LocalDateTime.now())) {
            // 기존 발급된 AuthSession 비활성화 isActive => false;
            authSession.deactivateAuthSession();

            // 새 AuthSession 발급
            return createAuthSession(member).getAccessToken();
        }

        return authSession.getAccessToken();
    }

    @Transactional
    public AuthSession createAuthSession(Member member) {
        AuthSession authSession = AuthSession.builder()
                                                .member(member)
                                                .build();
        authSessionRepository.save(authSession);
        return authSession;
    }

    @Transactional
    public void expireAuthSession(String accessToken) {
        AuthSession authSession = authSessionRepository.findByAccessToken(accessToken).orElse(null);
        if(authSession != null) {
            authSession.deactivateAuthSession();
        }
    }

}
