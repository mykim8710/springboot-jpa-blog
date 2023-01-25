package com.mykim.blog.auth.manual.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mykim.blog.auth.manual.domain.AuthSession;
import com.mykim.blog.auth.manual.dto.request.RequestAuthDto;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.repository.MemberRepository;
import com.mykim.blog.auth.manual.repository.AuthSessionRepository;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;

import static com.mykim.blog.global.result.error.ErrorCode.INVALID_SIGN_IN_INFO;
import static com.mykim.blog.global.result.error.ErrorCode.UNAUTHORIZED_MEMBER;
import static com.mykim.blog.global.result.SuccessCode.SIGN_OUT;
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
    AuthSessionRepository authSessionRepository;

    @Test
    @DisplayName("[성공] /api/v1/auth/sign-in POST 요청 시 로그인 성공")
    @Transactional
    void signInApiSuccessTest() throws Exception {
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

        RequestAuthDto memberSignInDto = RequestAuthDto.builder()
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
    @DisplayName("[실패] /api/v1/auth/sign-in POST 요청 시 유저정보가 틀리면 로그인 실패, InvalidSignInInfoException이 발생한다.")
    @Transactional
    void signInApiFailTest() throws Exception {
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

        RequestAuthDto authDto = RequestAuthDto.builder()
                                                    .email("aa@aa.com")
                                                    .password("aaaaa")
                                                    .build();

        String requestDtoJsonStr = objectMapper.writeValueAsString(authDto);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post(api)
                        .contentType(APPLICATION_JSON)
                        .content(requestDtoJsonStr)
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(INVALID_SIGN_IN_INFO.getStatus()))
                .andExpect(jsonPath("$.code").value(INVALID_SIGN_IN_INFO.getCode()))
                .andExpect(jsonPath("$.message").value(INVALID_SIGN_IN_INFO.getMessage()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("[성공] /api/v1/auth/sign-in POST 요청 시 유저정보가 일치하고 인증세션 객체가 없다면 인증세션 객체가 생성된다(새로운 Access Token이 발급되고 쿠키에 저장된다.).")
    @Transactional
    void signInApiCreateAuthSessionAndSaveCookieSuccessTest() throws Exception {
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

        AuthSession authSession = AuthSession.builder()
                                                .member(member)
                                                .build();
        authSessionRepository.save(authSession);


        String api = "/api/v1/auth/sign-in";
        RequestAuthDto authDto = RequestAuthDto.builder()
                                                .email(email)
                                                .password(password)
                                                .build();

        String requestDtoJsonStr = objectMapper.writeValueAsString(authDto);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post(api)
                                .contentType(APPLICATION_JSON)
                                .content(requestDtoJsonStr)
                        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.cookie().value("AUTHORIZATION_SESSION", authSessionRepository.findByMemberId(member.getId()).get().getAccessToken()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("[성공] /api/v1/auth/sign-in POST 요청 시 유저정보가 일치하고 인증세션 객체가 존재하지만 accessToken의 만료일이 지났다면 새로운 인증세션 객체가 생성된다(새로운 Access Token이 발급되고 쿠키에 저장한다.)")
    @Transactional
    void signInApiReCreateAuthSessionAndSaveCookieSuccessTest() throws Exception {
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

        AuthSession oldAuthSession = AuthSession.builder()
                                                .member(member)
                                                .build();
        authSessionRepository.save(oldAuthSession);
        oldAuthSession.deactivateAuthSession();

        String api = "/api/v1/auth/sign-in";
        RequestAuthDto authDto = RequestAuthDto.builder()
                                                    .email(email)
                                                    .password(password)
                                                    .build();

        String requestDtoJsonStr = objectMapper.writeValueAsString(authDto);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post(api)
                        .contentType(APPLICATION_JSON)
                        .content(requestDtoJsonStr)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.cookie().value("AUTHORIZATION_SESSION", authSessionRepository.findByMemberId(member.getId()).get().getAccessToken()))
                .andDo(MockMvcResultHandlers.print());

        Assertions.assertThat(oldAuthSession.isActive()).isEqualTo(false);
    }

    @Test
    @DisplayName("[실패] /api/v1/auth/foo GET 요청 시 로그인(인증)이 안되어있으면 UnAuthorizedMemberException이 발생한다.")
    @Transactional
    void fooAuthorizationTestApiFailTest() throws Exception {
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

    @Test
    @DisplayName("[성공] /api/v1/auth/foo GET 요청 시 정상 토큰값이 존재하는 상태에서 전송한 토큰이 유효하면 인증이 성공되어 원하는 값을 리턴받는다.")
    @Transactional
    void fooAuthorizationTestApiSuccessTest() throws Exception {
        // given
        String email = "vbvb@vbvb.com";
        String password = "1111";
        String username = "vbvb";

        Member member = Member.builder()
                                .email(email)
                                .username(username)
                                .password(password)
                                .build();
        memberRepository.save(member);

        AuthSession authSession = AuthSession.builder()
                                                .member(member)
                                                .build();
        authSessionRepository.save(authSession);

        String api = "/api/v1/auth/foo";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api)
                        //.header(HttpHeaders.COOKIE, "AUTHORIZATION_SESSION="+ // X
                        .cookie(new Cookie("AUTHORIZATION_SESSION", authSession.getAccessToken()))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("인증이 필요한 api : 인증성공"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("[성공] /api/v1/auth/sign-out GET 요청 시 로그아웃이 성공되며 생성된 토큰이 만료된다.")
    @Transactional
    void signOutApiSuccessTest() throws Exception {
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

        AuthSession authSession = AuthSession.builder()
                                                .member(member)
                                                .build();
        authSessionRepository.save(authSession);

        String api = "/api/v1/auth/sign-out";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api)
                        .contentType(APPLICATION_JSON)
                        .cookie(new Cookie("AUTHORIZATION_SESSION", authSession.getAccessToken()))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(SIGN_OUT.getStatus()))
                .andExpect(jsonPath("$.code").value(SIGN_OUT.getCode()))
                .andExpect(jsonPath("$.message").value(SIGN_OUT.getMessage()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());

        AuthSession findAuthToken = authSessionRepository.findByAccessToken(authSession.getAccessToken()).get();
        Assertions.assertThat(findAuthToken.isActive()).isFalse();
    }

}