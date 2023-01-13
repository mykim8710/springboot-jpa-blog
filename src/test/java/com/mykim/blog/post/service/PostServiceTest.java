package com.mykim.blog.post.service;

import com.mykim.blog.post.domain.Post;
import com.mykim.blog.post.dto.request.RequestPostCreateDto;
import com.mykim.blog.post.dto.response.ResponsePostSelectDto;
import com.mykim.blog.post.exception.NotFoundPostException;
import com.mykim.blog.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class PostServiceTest {
    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("[성공] PostService, createPost() 실행하면 글이 등록된다.")
    void createPostSuccessTest() throws Exception {
        // given
        RequestPostCreateDto requestPostCreateDto = RequestPostCreateDto
                                                                .builder()
                                                                .title("title")
                                                                .content("content")
                                                                .build();

        // when
        Long createdPostId = postService.createPost(requestPostCreateDto);

        // then
        Post findPost = em.find(Post.class, createdPostId);
        assertThat(requestPostCreateDto.getTitle()).isEqualTo(findPost.getTitle());
        assertThat(requestPostCreateDto.getContent()).isEqualTo(findPost.getContent());
    }

    @Test
    @DisplayName("[성공] PostService, selectPostById() 실행하면 글 하나가 조회된다.")
    void selectPostByIdSuccessTest() throws Exception {
        // given
        Post post = Post.builder()
                            .title("title")
                            .content("content")
                            .build();

        em.persist(post);
        em.flush();
        em.clear();

        // when
        ResponsePostSelectDto findPostDto = postService.selectPostById(post.getId());

        // then
        assertThat(findPostDto).isNotNull();
        assertThat(findPostDto.getTitle()).isEqualTo(post.getTitle());
        assertThat(findPostDto.getContent()).isEqualTo(post.getContent());
    }

    @Test
    @DisplayName("[실패] PostService, selectPostById() 실행하면 글 하나가 조회되지않고 exception 발생")
    void selectPostByIdFailTest() throws Exception {
        // given
        long postId = -1L;

        // when & then
        assertThatThrownBy(() -> {
            postService.selectPostById(postId);
        }).isInstanceOf(NotFoundPostException.class);
    }

    @Test
    @DisplayName("[성공] PostService, selectPostAll() 실행하면 글 전체가 조회된다.")
    void selectPostAllSuccessTest() throws Exception {
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

        // when
        List<ResponsePostSelectDto> responsePostSelectDtos = postService.selectPostAll();

        // then
        assertThat(responsePostSelectDtos).isNotNull();
        assertThat(responsePostSelectDtos.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("[성공] PostService, selectPostPagination() 실행하면 선택한 페이지의 글이 조회된다")
    void selectPostAllPaginationSuccessTest() throws Exception {
        // given
        List<Post> createdPosts = IntStream.range(1, 31)
                                            .mapToObj(i -> Post.builder()
                                                    .title("title_" +i)
                                                    .content("content_" +i)
                                                    .build()
                                            ).collect(Collectors.toList());
        postRepository.saveAll(createdPosts);

        int page = 0;   // 0부터 시작
        int size = 5;

        // when
        List<ResponsePostSelectDto> responsePostSelectDtos = postService.selectPostAllPagination(page, size);
        for (ResponsePostSelectDto responsePostSelectDto : responsePostSelectDtos) {
            System.out.println("responsePostSelectDto = " + responsePostSelectDto);
        }

        // then
        assertThat(responsePostSelectDtos).isNotNull();
        assertThat(responsePostSelectDtos.size()).isEqualTo(5);
        assertThat(responsePostSelectDtos.get(0).getTitle()).isEqualTo("title_1");
        assertThat(responsePostSelectDtos.get(4).getTitle()).isEqualTo("title_5");
    }


}