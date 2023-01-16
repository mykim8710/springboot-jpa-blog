package com.mykim.blog.member.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mykim.blog.global.error.ErrorCode;
import com.mykim.blog.global.response.SuccessCode;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.dto.request.RequestMemberInsertDto;
import com.mykim.blog.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@AutoConfigureMockMvc // @SpringBootTest에서 MockMvc 객체를 사용하기 위함
@SpringBootTest
class MemberApiControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("[성공] /api/v1/members POST 요청 시 새로운 사용자가 등록된다.")
    @Transactional
    void createMemberApiSuccessTest() throws Exception {
        // given
        String username = "test";
        String password = "1111";

        RequestMemberInsertDto memberInsertDto = RequestMemberInsertDto.builder()
                                                                        .username(username)
                                                                        .password(password)
                                                                        .build();

        String api = "/api/v1/members";
        String requestDtoJsonStr = objectMapper.writeValueAsString(memberInsertDto);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post(api)
                        .contentType(APPLICATION_JSON)
                        .content(requestDtoJsonStr)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.INSERT.getStatus()))
                .andExpect(jsonPath("$.code").value(SuccessCode.INSERT.getCode()))
                .andExpect(jsonPath("$.message").value(SuccessCode.INSERT.getMessage()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());

        // then
        long count = memberRepository.count();
        assertThat(count).isEqualTo(1);

        Member findMember = memberRepository.findAll().get(0);
        assertThat(memberInsertDto.getUsername()).isEqualTo(findMember.getUsername());
        assertThat(memberInsertDto.getPassword()).isEqualTo(findMember.getPassword());
    }

    @Test
    @DisplayName("[실패] /api/v1/members POST 요청 시 이미 등록된 사용자로 인해 DuplicateMemberUsernameException이 발생한다.")
    @Transactional
    void createMemberApiFailTest() throws Exception {
        // given
        String username = "test";
        String password = "1111";

        RequestMemberInsertDto memberInsertDto = RequestMemberInsertDto.builder()
                                                                            .username(username)
                                                                            .password(password)
                                                                            .build();
        Member member = Member.createMember(memberInsertDto);
        memberRepository.save(member);


        String api = "/api/v1/members";
        String requestDtoJsonStr = objectMapper.writeValueAsString(memberInsertDto);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post(api)
                        .contentType(APPLICATION_JSON)
                        .content(requestDtoJsonStr)
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(ErrorCode.DUPLICATE_USERNAME.getStatus()))
                .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATE_USERNAME.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_USERNAME.getMessage()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("[실패] /api/v1/members POST 요청 시 dto validation 실패로  발생한다.")
    @Transactional
    void createMemberApiValidationFailTest() throws Exception {
        // given
        String username = "";
        String password = null;

        RequestMemberInsertDto memberInsertDto = RequestMemberInsertDto.builder()
                                                                            .username(username)
                                                                            .password(password)
                                                                            .build();

        String api = "/api/v1/members";
        String requestDtoJsonStr = objectMapper.writeValueAsString(memberInsertDto);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post(api)
                        .contentType(APPLICATION_JSON)
                        .content(requestDtoJsonStr)
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(ErrorCode.VALIDATION_ERROR.getStatus()))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }
}