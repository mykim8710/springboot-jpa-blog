package com.mykim.blog.post.service;

import com.mykim.blog.post.domain.Post;
import com.mykim.blog.post.dto.request.RequestPostCreateDto;
import com.mykim.blog.post.dto.response.ResponsePostSelectDto;
import com.mykim.blog.post.exception.NotFoundPostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class PostServiceTest {
    @Autowired
    PostService postService;

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

}