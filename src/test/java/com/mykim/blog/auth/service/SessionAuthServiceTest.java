package com.mykim.blog.auth.service;

import com.mykim.blog.auth.manual.domain.AuthSession;
import com.mykim.blog.auth.manual.repository.AuthSessionRepository;
import com.mykim.blog.auth.manual.service.SessionAuthService;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class SessionAuthServiceTest {
    @Autowired
    SessionAuthService authorizationService;

    @Autowired
    AuthSessionRepository authorizationSessionRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("[성공] SessionAuthService createAuthorizationSession() 실행하면 새로운 AuthorizationSession이 등록된다.")
    void createAuthorizationSessionSuccessTest() {
        // given
        String email = "aaa@aaa.com";
        String password = "1234";
        String username = "aaa";

        Member member = Member.builder()
                                    .email(email)
                                    .username(username)
                                    .password(password)
                                    .build();
        memberRepository.save(member);

        // when
        AuthSession authorizationSession = authorizationService.createAuthSession(member);

        // then
        AuthSession findAuthSession = authorizationSessionRepository.findById(authorizationSession.getId()).get();
        Assertions.assertThat(findAuthSession.getAccessToken()).isEqualTo(authorizationSession.getAccessToken());
    }


}