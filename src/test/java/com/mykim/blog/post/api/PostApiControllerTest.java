package com.mykim.blog.post.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mykim.blog.global.error.ErrorCode;
import com.mykim.blog.global.response.SuccessCode;
import com.mykim.blog.post.domain.Post;
import com.mykim.blog.post.dto.request.RequestPostCreateDto;
import com.mykim.blog.post.repository.PostRepository;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc // @SpringBootTest에서 MockMvc 객체를 사용하기 위함
@SpringBootTest
class PostApiControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostRepository postRepository;

    @Test
    @DisplayName("[성공] /api/v1/posts POST 요청 시 글등록이 된다.")
    @Transactional
    void createPostApiSuccessTest() throws Exception {
        // given
        String api = "/api/v1/posts";
        RequestPostCreateDto requestPostCreateDto = RequestPostCreateDto
                                                                    .builder()
                                                                    .title("title")
                                                                    .content("content")
                                                                    .build();
        String requestDtoJsonStr = objectMapper.writeValueAsString(requestPostCreateDto);

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
        long count = postRepository.count();
        assertThat(count).isEqualTo(1);

        Post findPost = postRepository.findAll().get(0);
        assertThat(requestPostCreateDto.getTitle()).isEqualTo(findPost.getTitle());
        assertThat(requestPostCreateDto.getContent()).isEqualTo(findPost.getContent());
    }


    @Test
    @DisplayName("[성공] /api/v1/posts/{postId} GET 요청 시 글 하나가 조회된다.")
    @Transactional
    void selectPostByIdSuccessApi() throws Exception {
        // given
        String title = "title";
        String content = "content";

        Post post = Post.builder()
                            .title(title)
                            .content(content)
                            .build();

        Post savePost = postRepository.save(post);

        String api = "/api/v1/posts/" +savePost.getId();

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.COMMON.getStatus()))
                .andExpect(jsonPath("$.code").value(SuccessCode.COMMON.getCode()))
                .andExpect(jsonPath("$.message").value(SuccessCode.COMMON.getMessage()))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.title").value(title))
                .andExpect(jsonPath("$.data.content").value(content))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("[실패] /api/v1/posts/{postId} GET 요청 시 글 하나가 조회되지 않고 실패한다.")
    @Transactional
    void selectPostByIdFailApi() throws Exception {
        // given
        String api = "/api/v1/posts/-1";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(ErrorCode.NOT_FOUND_POST.getStatus()))
                .andExpect(jsonPath("$.code").value(ErrorCode.NOT_FOUND_POST.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_POST.getMessage()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @DisplayName("[성공] /api/v1/posts GET 요청 시 글 전체가 조회된다.")
    @Transactional
    void selectPostAllSuccessApi() throws Exception {
        // given
        postRepository.saveAll(List.of(
                Post.builder()
                        .title("title")
                        .content("content")
                        .build(),
                Post.builder()
                        .title("title2")
                        .content("content2")
                        .build(),
                Post.builder()
                        .title("title3")
                        .content("content3")
                        .build()
        ));

        String api = "/api/v1/posts";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.COMMON.getStatus()))
                .andExpect(jsonPath("$.code").value(SuccessCode.COMMON.getCode()))
                .andExpect(jsonPath("$.message").value(SuccessCode.COMMON.getMessage()))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.length()", Matchers.is(3)))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @DisplayName("[성공] /api/v2/posts GET 요청 시 선택한 페이지의 글이 조회된다.")
    @Transactional
    void selectPostAllPaginationSuccessApi() throws Exception {
        // given
        /**
         * 이 코드를 람다식을 사용해서 아래와 같이 사용할 수 있다.
         for (int i = 0; i < 30>; i++) {
         Post post = Post.builder().title("title_"+i).content("content_"+i).build();
         createdPosts.add(post);
         }
         **/

        List<Post> createdPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                                    .title("title_" +i)
                                    .content("content_" +i)
                                    .build()
                ).collect(Collectors.toList());

        postRepository.saveAll(createdPosts);


        String api = "/api/v2/posts";

        // sql limit, offset, sort
        int page = 0;
        int size = 5;
        String sort = "id,asc";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api)
                        .queryParam("page", String.valueOf(page))
                        .queryParam("size", String.valueOf(size))
                        .queryParam("sort", sort)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.COMMON.getStatus()))
                .andExpect(jsonPath("$.code").value(SuccessCode.COMMON.getCode()))
                .andExpect(jsonPath("$.message").value(SuccessCode.COMMON.getMessage()))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.content.length()", Matchers.is(size)))
                .andExpect(jsonPath("$.data.content[0].title").value("title_1"))
                .andExpect(jsonPath("$.data.content[4].title").value("title_5"))
                .andDo(MockMvcResultHandlers.print());
    }
}