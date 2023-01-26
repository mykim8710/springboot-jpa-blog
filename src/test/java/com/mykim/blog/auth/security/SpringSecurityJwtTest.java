package com.mykim.blog.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mykim.blog.global.result.error.ErrorCode;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.domain.MemberRole;
import com.mykim.blog.member.dto.request.RequestMemberSignInDto;
import com.mykim.blog.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static com.mykim.blog.global.result.SuccessCode.SIGN_IN;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Slf4j
@AutoConfigureMockMvc // @SpringBootTest에서 MockMvc 객체를 사용하기 위함
@SpringBootTest
@Transactional
public class SpringSecurityJwtTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Test
    @DisplayName("[성공] /sign-in POST 요청 시 인증에 성공하면 jwt 토큰을 발급받는다.")
    void springSecurityAuthenticationReturnJwtSuccessTest() throws Exception{
        // given
        String email = "abc@abc.com";
        String password = "1111";

        Member member = createMember(email, password);

        String api = "/sign-in";
        RequestMemberSignInDto requestMemberSignInDto = RequestMemberSignInDto.builder()
                                                                                .email(email)
                                                                                .password(password)
                                                                                .build();

        String requestDtoJsonStr = objectMapper.writeValueAsString(requestMemberSignInDto);

        // when & then
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(api)
                        .contentType(APPLICATION_JSON)
                        .content(requestDtoJsonStr)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(SIGN_IN.getStatus()))
                .andExpect(jsonPath("$.code").value(SIGN_IN.getCode()))
                .andExpect(jsonPath("$.message").value(SIGN_IN.getMessage()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());

        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String jwt = response.getHeader("Authorization");
        log.info("jwt token = {}", jwt);
    }

    @Test
    @DisplayName("[성공]  /api/auth/member get 요청 시 권한인증에 성공하면 원하는 값을 리턴받는다.")
    void springSecurityAuthorizationSuccessTest() throws Exception {
        // given
        String email = "abc@abc.com";
        String password = "1111";
        createMember(email, password);
        String jwt = memberSignInReturnJwt(email, password);
        String api = "/api/auth/member";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api)
                                .header("Authorization", jwt)
                        )
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string("member"))
                        .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("[실패]  /api/auth/member get 요청 시 jwt token을 전송하지 않으면 401 error가 발생한다.")
    void springSecurityAuthorization401FailTest() throws Exception {
        // given
        String email = "abc@abc.com";
        String password = "1111";
        createMember(email, password);
        String jwt = memberSignInReturnJwt(email, password);
        String api = "/api/auth/member";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api)

                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(ErrorCode.UNAUTHORIZED_MEMBER.getStatus()))
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_MEMBER.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHORIZED_MEMBER.getMessage()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("[실패]  /api/auth/admin get 요청 시 member권한으로 권한이 없는 admin api 접근시 403 error를 발생한다.")
    void springSecurityAuthorization403FailTest() throws Exception {
        // given
        String email = "abc@abc.com";
        String password = "1111";
        createMember(email, password);
        String jwt = memberSignInReturnJwt(email, password);
        String api = "/api/auth/admin";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api)
                        .header("Authorization", jwt)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(jsonPath("$.status").value(ErrorCode.ACCESS_DENIED.getStatus()))
                .andExpect(jsonPath("$.code").value(ErrorCode.ACCESS_DENIED.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.ACCESS_DENIED.getMessage()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    private String memberSignInReturnJwt(String email, String password) throws Exception {
        String signInApi = "/sign-in";
        RequestMemberSignInDto requestMemberSignInDto = RequestMemberSignInDto.builder()
                .email(email)
                .password(password)
                .build();

        String requestDtoJsonStr = objectMapper.writeValueAsString(requestMemberSignInDto);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(signInApi)
                        .contentType(APPLICATION_JSON)
                        .content(requestDtoJsonStr)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String jwt = response.getHeader("Authorization");
        log.info("jwt : {}", jwt);
        return jwt;
    }
    private Member createMember(String email, String password) {
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .username("abc")
                .memberRole(MemberRole.ROLE_MEMBER)
                .build();

        memberRepository.save(member);

        return member;
    }


}
