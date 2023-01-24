package com.mykim.blog.member.service;

import com.mykim.blog.global.result.error.ErrorCode;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.dto.request.RequestMemberInsertDto;
import com.mykim.blog.member.exception.DuplicateMemberEmailException;
import com.mykim.blog.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MemberServiceTest {
    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("[성공] MemberService, createMember() 실행하면 새로운 사용자가 등록된다.")
    @Transactional
    void signUpMemberSuccessTest() throws Exception {
        // given
        String email = "test@test.com";
        String password = "1111";
        String username = "test";

        RequestMemberInsertDto memberInsertDto = RequestMemberInsertDto.builder()
                                                                        .email(email)
                                                                        .username(username)
                                                                        .password(password)
                                                                        .build();

        // when
        Long createdMemberId = memberService.signUpMember(memberInsertDto);

        // then
        Member findMember = memberRepository.findById(createdMemberId).get();
        assertThat(findMember.getEmail()).isEqualTo(email);
        assertThat(findMember.getPassword()).isEqualTo(password);
    }

    @Test
    @DisplayName("[실패] MemberService, createMember() 실행하면 이미 등록된 사용자로 인해 DuplicateMemberUsernameException이 발생한다.")
    @Transactional
    void signUpMemberFailTest() throws Exception {
        // given
        String email = "test@test.com";
        String password = "1111";
        String username = "test";

        RequestMemberInsertDto memberInsertDto = RequestMemberInsertDto.builder()
                                                                            .email(email)
                                                                            .username(username)
                                                                            .password(password)
                                                                            .build();
        memberService.signUpMember(memberInsertDto);

        // when & then
        RequestMemberInsertDto memberInsertDto2 = RequestMemberInsertDto.builder()
                                                                            .email(email)
                                                                            .username(username)
                                                                            .password(password)
                                                                            .build();

        assertThatThrownBy(() -> memberService.signUpMember(memberInsertDto2))
                .isInstanceOf(DuplicateMemberEmailException.class)
                .hasMessage(ErrorCode.DUPLICATE_USER_EMAIL.getMessage());
    }

}
