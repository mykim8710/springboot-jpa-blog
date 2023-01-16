package com.mykim.blog.member.service;

import com.mykim.blog.global.error.ErrorCode;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.dto.request.RequestMemberInsertDto;
import com.mykim.blog.member.exception.DuplicateMemberUsernameException;
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
    void createMemberSuccessTest() throws Exception {
        // given
        String username = "test";
        String password = "1111";

        RequestMemberInsertDto memberInsertDto = RequestMemberInsertDto.builder()
                                                                        .username(username)
                                                                        .password(password)
                                                                        .build();

        // when
        Long createdMemberId = memberService.createMember(memberInsertDto);

        // then
        Member findMember = memberRepository.findById(createdMemberId).get();
        assertThat(findMember.getUsername()).isEqualTo(username);
        assertThat(findMember.getPassword()).isEqualTo(password);
    }

    @Test
    @DisplayName("[실패] MemberService, createMember() 실행하면 이미 등록된 사용자로 인해 DuplicateMemberUsernameException이 발생한다.")
    @Transactional
    void createMemberFailTest() throws Exception {
        // given
        String username = "test";
        String password = "1111";

        RequestMemberInsertDto memberInsertDto = RequestMemberInsertDto.builder()
                                                                            .username(username)
                                                                            .password(password)
                                                                            .build();
        memberService.createMember(memberInsertDto);

        // when & then
        RequestMemberInsertDto memberInsertDto2 = RequestMemberInsertDto.builder()
                                                                            .username(username)
                                                                            .password(password)
                                                                            .build();

        assertThatThrownBy(() -> memberService.createMember(memberInsertDto2))
                .isInstanceOf(DuplicateMemberUsernameException.class)
                .hasMessage(ErrorCode.DUPLICATE_USERNAME.getMessage());
    }

}
