package com.mykim.blog.member.service;

import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.domain.MemberSession;
import com.mykim.blog.member.dto.request.RequestMemberSignInDto;
import com.mykim.blog.member.exception.InvalidSignInInfoException;
import com.mykim.blog.member.repository.MemberRepository;
import com.mykim.blog.member.repository.MemberSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

import static com.mykim.blog.global.error.ErrorCode.INVALID_SIGN_IN_INFO;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final MemberSessionRepository memberSessionRepository;

    @Transactional
    public void signInMember(RequestMemberSignInDto signInDto, HttpServletResponse response) {
        Member member = memberRepository.findByEmailAndPassword(signInDto.getEmail(), signInDto.getPassword())
                                            .orElseThrow(() -> new InvalidSignInInfoException(INVALID_SIGN_IN_INFO));

        /**
         *  accessToken 검증
         */
        MemberSession memberSession = member.getMemberSession();

        // session 객체가 없거나 세션의 만료시간이 지났다면 : 새로운 토큰 발급
        if(memberSession == null) {
            memberSession = member.addSession();
        }

        // 세션의 만료시간이 지났다면 : 기존 발급토큰 삭제 후 새로운 토큰 발급
        if(memberSession != null && memberSession.getTokenExpirationTime().isBefore(LocalDateTime.now())) {
            memberSessionRepository.delete(memberSession);  // 기존 발급된 토큰 삭제
            memberSession = member.addSession();
        }

        Cookie cookie = new Cookie("ACCESS_TOKEN", memberSession.getAccessToken());
        response.addCookie(cookie);
    }


}
