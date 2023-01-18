package com.mykim.blog.global.authorization.service;

import com.mykim.blog.global.authorization.domain.AuthorizationSession;
import com.mykim.blog.global.authorization.repository.AuthorizationSessionRepository;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthorizationServiceTest {
    @Autowired
    AuthorizationService authorizationService;

    @Autowired
    AuthorizationSessionRepository authorizationSessionRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("[성공] AuthorizationService createAuthorizationSession() 실행하면 새로운 AuthorizationSession이 등록된다.")
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
        AuthorizationSession authorizationSession = authorizationService.createAuthorizationSession(member);

        // then
        AuthorizationSession findAuthSession = authorizationSessionRepository.findById(authorizationSession.getId()).get();
        Assertions.assertThat(findAuthSession.getAccessToken()).isEqualTo(authorizationSession.getAccessToken());
    }


}