package com.mykim.blog.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mykim.blog.dto.RequestTestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest // MockMvc 사용하기 위함
class TestApiControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("/api/test get 요청시 Hello World를 반환")
    void getApiMethodTest() throws Exception {
        // given
        String api = "/api/test";
        String expectedResult = "Hello World";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResult))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("/api/test post, application/x-www-form-urlencoded 형태 요청시 Hello World를 반환")
    void postApiMethodTest() throws Exception {
        // given
        String api = "/api/test";
        String expectedResult = "Hello World";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post(api)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("title", "글제목")
                        .param("content", "글내용")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResult))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("/api/test post, application/json 형태 요청시 Hello World를 반환")
    void postApiJsonMethodTest() throws Exception {
        // given
        String api = "/api/test/json";
        String expectedResult = "Hello World";
        String requestTestDtoJsonStr = objectMapper.writeValueAsString(new RequestTestDto("글제목", "글내용"));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post(api)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestTestDtoJsonStr)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResult))
                .andDo(MockMvcResultHandlers.print());
    }
}