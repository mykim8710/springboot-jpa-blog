package com.mykim.blog.tip.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc
@SpringBootTest
class ExportApiControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("[성공] /api/v2/export GET 요청 시 원하는 타입을 반환한다.")
    void exportApiV2Test() throws Exception{
        // given
        String api = "/api/v2/export";
        String type = "PDF";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api)
                        .queryParam("type", type)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("PDF파일"))
                    .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @DisplayName("[성공] /api/v3/export GET 요청 시 원하는 타입을 반환한다.")
    void exportApiV3Test() throws Exception{
        // given
        String api = "/api/v3/export";
        String type = "HWP";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api)
                        .queryParam("type", type)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("한글파일"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("[성공] /api/v4/export GET 요청 시 원하는 타입을 반환한다.")
    void exportApiV4Test() throws Exception{
        // given
        String api = "/api/v4/export";
        String type = "EXCEL";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api)
                        .queryParam("type", type)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("EXCEL파일"))
                .andDo(MockMvcResultHandlers.print());
    }


}