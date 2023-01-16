package com.mykim.blog.member.service;

import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.dto.request.RequestMemberInsertDto;
import com.mykim.blog.member.exception.DuplicateMemberUsernameException;
import com.mykim.blog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.mykim.blog.global.error.ErrorCode.DUPLICATE_USERNAME;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Long createMember(RequestMemberInsertDto memberInsertDto) {
        duplicateMemberUsername(memberInsertDto.getUsername()); // validation : duplicate username
        Member member = Member.createMember(memberInsertDto);
        memberRepository.save(member);
        return member.getId();
    }

    private void duplicateMemberUsername(String username) {
        Optional<Member> byUsername = memberRepository.findByUsername(username);
        if(byUsername.isPresent()) {
            throw new DuplicateMemberUsernameException(DUPLICATE_USERNAME);
        }
    }


}
