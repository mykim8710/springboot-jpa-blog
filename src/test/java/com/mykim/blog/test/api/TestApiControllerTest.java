package com.mykim.blog.test.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mykim.blog.global.error.ErrorCode;
import com.mykim.blog.post.service.PostService;
import com.mykim.blog.test.dto.RequestTestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest // MockMvc 사용하기 위함
class TestApiControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @Test
    @DisplayName("/api/test GET 요청시 Hello World를 반환한다.")
    void getApiTest() throws Exception {
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
    @DisplayName("/api/test/form POST(application/x-www-form-urlencoded) 요청시 Hello World를 반환한다.")
    void postFormApiTest() throws Exception {
        // given
        String api = "/api/test/form";
        String expectedResult = "Hello World";
        String title = "글제목";
        String content = "글내용";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post(api)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("title", title)
                        .param("content", content)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResult))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("/api/test/json POST(application/json) 형태 요청시 Hello World를 반환")
    void postJsonApiTest() throws Exception {
        // given
        String api = "/api/test/json";
        String expectedResult = "Hello World";
        String requestTestDtoJsonStr = objectMapper.writeValueAsString(RequestTestDto
                                                                                .builder()
                                                                                .title("글제목")
                                                                                .content("글내용")
                                                                                .build());

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post(api)
                        .contentType(APPLICATION_JSON)
                        .content(requestTestDtoJsonStr)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResult))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("/api/test/validation POST(BindingResult) 요청 시 requestDto에 title, content 값은 필수이다.")
    void postJsonRequestDataValidationApiTest() throws Exception {
        // given
        String api = "/api/test/validation";
        String requestTestDtoJsonStr = objectMapper.writeValueAsString(RequestTestDto
                                                                                .builder()
                                                                                .title("")
                                                                                .content(null)
                                                                                .build());

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post(api)
                        .contentType(APPLICATION_JSON)
                        .content(requestTestDtoJsonStr)
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(jsonPath("$.title").value("제목이 없습니다."))
                .andExpect(jsonPath("$.content").value("내용이 없습니다."))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("/api/test/validation POST(Global Exception Handler) 요청 시 requestDto에 title, content 값은 필수이다.")
    void postJsonRequestDataValidationGlobalExceptionApiTest() throws Exception {
        // given
        String api = "/api/test/validation/global-exception";
        String requestTestDtoJsonStr = objectMapper.writeValueAsString(RequestTestDto
                                                                                .builder()
                                                                                .title("")
                                                                                .content(null)
                                                                                .build());

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post(api)
                        .contentType(APPLICATION_JSON)
                        .content(requestTestDtoJsonStr)
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(ErrorCode.VALIDATION_ERROR.getStatus()))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.VALIDATION_ERROR.getMessage()))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

}