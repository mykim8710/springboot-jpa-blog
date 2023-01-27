package com.mykim.blog.post.api;

import com.mykim.blog.post.domain.Post;
import com.mykim.blog.post.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Slf4j
@AutoConfigureMockMvc // @SpringBootTest에서 MockMvc 객체를 사용하기 위함
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.blog.mykim.com", uriPort = 443)
@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)   // Spring Rest Doc
class PostApiControllerDocTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PostRepository postRepository;

    // MockMvc 객체 초기화
    /*@BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                                       .apply(documentationConfiguration(restDocumentation))
                                       .build();
    }*/

    @Test
    @DisplayName("[성공] 인증필요없음  /api/v1/posts/{postId} GET 요청 시 글 하나가 조회된다.")
    @Transactional
    void selectPostByIdApiSuccessTest() throws Exception {
        // given
        Post post = Post.builder()
                            .title("title")
                            .content("content")
                            .member(null)
                            .build();

        postRepository.save(post);

        String api = "/api/v1/posts/{postId}";

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get(api, post.getId())
                        .contentType(APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("post_selectOne",
                        // path parameter
                        RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("postId").description("게시물 id")),
                        // response field
                        PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("status").description("http status"),
                            PayloadDocumentation.fieldWithPath("code").description(""),
                            PayloadDocumentation.fieldWithPath("message").description("message"),
                            PayloadDocumentation.fieldWithPath("data.id").description("게시물 ID"),
                            PayloadDocumentation.fieldWithPath("data.title").description("게시물 Title"),
                            PayloadDocumentation.fieldWithPath("data.content").description("게시물 Content"),
                            PayloadDocumentation.fieldWithPath("data.createdDate").description("게시물 작성일"),
                            PayloadDocumentation.fieldWithPath("data.lastModifiedDate").description("게시물 최종수정일"),
                            PayloadDocumentation.fieldWithPath("data.username").description("게시물 작성자명")
                        )

                        ));// rest doc 문서화;
    }

}