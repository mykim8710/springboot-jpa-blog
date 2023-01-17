package com.mykim.blog.member.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mykim.blog.global.error.ErrorCode;
import com.mykim.blog.global.response.SuccessCode;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.domain.MemberSession;
import com.mykim.blog.member.dto.request.RequestMemberSignInDto;
import com.mykim.blog.member.repository.MemberRepository;
import com.mykim.blog.member.repository.MemberSessionRepository;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static com.mykim.blog.global.error.ErrorCode.UNAUTHORIZED_MEMBER;
import static com.mykim.blog.global.response.SuccessCode.SIGN_IN;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc // @SpringBootTest에서 MockMvc 객체를 사용하기 위함
@SpringBootTest
class AuthApiControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberSessionRepository memberSessionRepository;

    @Test
    @DisplayName("[성공] /api/v1/auth/sign-in POST 요청 시 로그인 성공")
    void signInMemberApiSuccessTest() throws Exception {
        // given
        String email = "abc@abc.com";
        String password = "1111";
        String username = "abc";

        Member member = Member.builder()
                                .email(email)
                                .username(username)
                                .password(password)
                                .build();
        memberRepository.save(member);

        String api = "/api/v1/auth/sign-in";

        RequestMemberSignInDto memberSignInDto = RequestMemberSignInDto.builder()
                                                                            .email(email)
                                                                            .password(password)
                                                                            .build();

        String requestDtoJsonStr = objectMapper.writeValueAsString(memberSignInDto);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post(api)
                        .contentType(APPLICATION_JSON)
                        .content(requestDtoJsonStr)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("[성공] /api/v1/auth/sign-in POST 요청 시 로그인 성공 후 세션객체가 하나 생성되고 Access Token이 발급된다.")
    void signInMemberApiCreateSessionSuccessTest() throws Exception {
        // given
        String email = "abc@abc.com";
        String password = "1111";
        String username = "abc";

        Member member = Member.builder()
                                .email(email)
                                .username(username)
                                .password(password)
                                .build();

        MemberSession memberSession = member.addSession();
        memberRepository.save(member);

        String api = "/api/v1/auth/sign-in";
        RequestMemberSignInDto memberSignInDto = RequestMemberSignInDto.builder()
                                                                        .email(email)
                                                                        .password(password)
                                                                        .build();

        String requestDtoJsonStr = objectMapper.writeValueAsString(memberSignInDto);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post(api)
                                .contentType(APPLICATION_JSON)
                                .content(requestDtoJsonStr)
                        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists("Set-Cookie"))
                .andExpect(MockMvcResultMatchers.cookie().exists("ACCESS_TOKEN"))
                .andDo(MockMvcResultHandlers.print());



        // then
        long count = memberSessionRepository.count();
        Assertions.assertThat(count).isEqualTo(1);

        Member findMember = memberRepository.findByEmail(email).get();
        String accessToken = findMember.getMemberSession().getAccessToken();
        Assertions.assertThat(accessToken).isEqualTo(memberSession.getAccessToken());
    }

    @Test
    @DisplayName("[실패] /api/auth/foo GET 요청 시 로그인(인증)이 안되 UnAuthorizedMemberException이 발생한다.")
    void fooAuthTestApiFailTest() throws Exception {
        // given
        String api = "/api/v1/auth/foo";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(UNAUTHORIZED_MEMBER.getStatus()))
                .andExpect(jsonPath("$.code").value(UNAUTHORIZED_MEMBER.getCode()))
                .andExpect(jsonPath("$.message").value(UNAUTHORIZED_MEMBER.getMessage()))
                .andExpect(jsonPath("$.data", Matchers.nullValue()))
                .andDo(MockMvcResultHandlers.print());
    }

//    @Test
//    @DisplayName("[성공] /api/auth/foo GET 요청 시 로그인(인증)이 성공하면 원하는 값을 리턴받는다.")
//    @Transactional
//    void fooAuthTestApiSuccessTest() throws Exception {
//        // given
//        String email = "vbvb@vbvb.com";
//        String password = "1111";
//        String username = "vbvb";
//
//        Member member = Member.builder()
//                                    .email(email)
//                                    .username(username)
//                                    .password(password)
//                                    .build();
//
//        MemberSession memberSession = member.addSession();
//        memberRepository.save(member);
//
//        String api = "/api/v1/auth/foo";
//
//        // when & then
//        mockMvc.perform(MockMvcRequestBuilders.get(api)
//                        .header("Authorization", memberSession.getAccessToken())
//                )
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string("인증이 필요한 페이지"))
//                .andDo(MockMvcResultHandlers.print());
//    }
}