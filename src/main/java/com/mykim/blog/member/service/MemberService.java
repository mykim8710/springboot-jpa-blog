package com.mykim.blog.member.service;

import com.mykim.blog.auth.security.jwt.JwtProvider;
import com.mykim.blog.global.result.error.ErrorCode;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.dto.request.RequestMemberInsertDto;
import com.mykim.blog.member.exception.DuplicateMemberEmailException;
import com.mykim.blog.member.exception.UnAuthorizedMemberException;
import com.mykim.blog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.mykim.blog.global.result.error.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final RedisTemplate redisTemplate;
    private final JwtProvider jwtProvider;

    @Transactional
    public Long signUpMember(RequestMemberInsertDto memberInsertDto) {
        duplicateMemberEmail(memberInsertDto.getEmail()); // validation : duplicate username
        Member member = Member.createMember(memberInsertDto);
        memberRepository.save(member);
        return member.getId();
    }

    private void duplicateMemberEmail(String email) {
        Optional<Member> byUsername = memberRepository.findByEmail(email);
        if(byUsername.isPresent()) {
            throw new DuplicateMemberEmailException(DUPLICATE_USER_EMAIL);
        }
    }


    public void expireJwt(HttpServletRequest request){
        String jwt = jwtProvider.resolveJwt(request);
        if(ObjectUtils.isEmpty(jwt)) {
            throw new UnAuthorizedMemberException(ErrorCode.UNAUTHORIZED_MEMBER);
        }

        Long jwtRemainValidTime = jwtProvider.getJwtRemainValidTime(jwt);   // redis에서의 만료시간(== 원래 토큰의 만료일자)
        redisTemplate.opsForValue().set(jwt, "sign-out", jwtRemainValidTime, TimeUnit.MILLISECONDS);
    }

}
