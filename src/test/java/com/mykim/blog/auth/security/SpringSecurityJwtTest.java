package com.mykim.blog.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.domain.MemberRole;
import com.mykim.blog.member.dto.request.RequestMemberSignInDto;
import com.mykim.blog.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@AutoConfigureMockMvc // @SpringBootTest에서 MockMvc 객체를 사용하기 위함
@SpringBootTest
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
    @DisplayName("[POST ]/sign-in test")
    @Transactional
    void test() throws Exception{
        // given
        String email = "abc@abc.com";
        String password = "1111";

        Member member = Member.builder()
                                .email(email)
                                .password(passwordEncoder.encode(password))
                                .username("abc")
                                .memberRole(MemberRole.ROLE_MEMBER)
                                .build();

        memberRepository.save(member);

        String api = "/sign-in";
        RequestMemberSignInDto requestMemberSignInDto = RequestMemberSignInDto.builder()
                                                                                .email(email)
                                                                                .password(password)
                                                                                .build();

        String requestDtoJsonStr = objectMapper.writeValueAsString(requestMemberSignInDto);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(api)
                        .contentType(APPLICATION_JSON)
                        .content(requestDtoJsonStr)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String authorization = response.getHeader("Authorization");
        System.out.println("authorization = " + authorization);


    }


}
