package com.mykim.blog.member.service;

import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.dto.request.RequestMemberInsertDto;
import com.mykim.blog.member.exception.DuplicateMemberEmailException;
import com.mykim.blog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.mykim.blog.global.error.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

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
}
