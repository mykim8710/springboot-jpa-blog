package com.mykim.blog.post.service;

import com.mykim.blog.global.result.error.exception.NotFoundException;
import com.mykim.blog.global.pagination.CustomPaginationRequest;
import com.mykim.blog.global.pagination.CustomSortingRequest;
import com.mykim.blog.post.domain.Post;
import com.mykim.blog.post.dto.request.RequestPostCreateDto;
import com.mykim.blog.post.dto.request.RequestPostUpdateDto;
import com.mykim.blog.post.dto.response.ResponsePostSelectDto;
import com.mykim.blog.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.mykim.blog.global.result.error.ErrorCode.NOT_FOUND_POST;
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
    @DisplayName("[실패] PostService, selectPostById() 실행하면 글 하나가 조회되지않고 NotFoundPostException 발생")
    void selectPostByIdFailTest() throws Exception {
        // given
        long postId = -1L;

        // when & then
        assertThatThrownBy(() -> postService.selectPostById(postId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(NOT_FOUND_POST.getMessage());
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

        // then\
        assertThat(responsePostSelectDtos).isNotNull();
        assertThat(responsePostSelectDtos.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("[성공] PostService, selectPostAllPagination() 실행하면 선택한 페이지의 글이 조회된다")
    void selectPostAllPaginationSuccessTest() throws Exception {
        // given
        List<Post> createdPosts = IntStream.range(1, 31)
                                            .mapToObj(i -> Post.builder()
                                                    .title("title_" +i)
                                                    .content("content_" +i)
                                                    .build()
                                            ).collect(Collectors.toList());
        postRepository.saveAll(createdPosts);

        int page = 0;   // == offset, 0부터 시작
        int size = 5;   // == limit
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        PageRequest request = PageRequest.of(page, size, sort);

        // when
        Page<ResponsePostSelectDto> responsePostSelectDtos = postService.selectPostAllPagination(request);

        // then
        assertThat(responsePostSelectDtos).isNotNull();
        assertThat(responsePostSelectDtos.getContent().size()).isEqualTo(size);
        assertThat(responsePostSelectDtos.getContent().get(0).getTitle()).isEqualTo("title_1");
        assertThat(responsePostSelectDtos.getContent().get(4).getTitle()).isEqualTo("title_5");
    }

    @Test
    @DisplayName("[성공] PostService, selectPostAllPaginationQuerydsl() 실행하면 선택한 페이지의 글이 조회된다")
    void selectPostAllPaginationQuerydslSuccessTest() throws Exception {
        // given
        List<Post> createdPosts = IntStream.range(1, 31)
                                            .mapToObj(i -> Post.builder()
                                                    .title("title_" +i)
                                                    .content("content_" +i)
                                                    .build()
                                            ).collect(Collectors.toList());
        postRepository.saveAll(createdPosts);


        int page = 2;
        int size = 10;
        CustomPaginationRequest paginationDto = CustomPaginationRequest.builder()
                                                                                .page(page)
                                                                                .size(size)
                                                                                .build();
        String sort = "id,desc";
        CustomSortingRequest sortingDto = CustomSortingRequest.builder()
                                                                        .sort(sort)
                                                                        .build();

        String keyword = "";

        // when
        Page<ResponsePostSelectDto> responsePostSelectDtos = postService.selectPostAllPaginationQuerydsl(paginationDto, sortingDto, keyword);

        // then
        assertThat(responsePostSelectDtos).isNotNull();
        assertThat(responsePostSelectDtos.getContent().size()).isEqualTo(size);
        assertThat(responsePostSelectDtos.getContent().get(0).getTitle()).isEqualTo("title_20");
        assertThat(responsePostSelectDtos.getContent().get(4).getTitle()).isEqualTo("title_16");
        assertThat(responsePostSelectDtos.getContent().get(9).getTitle()).isEqualTo("title_11");
    }

    @Test
    @DisplayName("[성공] PostService, editPostById() 실행하면 글이 수정된다.")
    void editPostByIdSuccessTest() throws Exception {
        // given
        RequestPostCreateDto requestPostCreateDto = RequestPostCreateDto
                                                                .builder()
                                                                .title("title")
                                                                .content("content")
                                                                .build();
        Post post = Post.createPost(requestPostCreateDto);
        postRepository.save(post);

        String updateTitle = "update_title";
        String updateContent = "update_content";
        RequestPostUpdateDto postUpdateDto = RequestPostUpdateDto
                                                        .builder()
                                                        .title(updateTitle)
                                                        .content(updateContent)
                                                        .build();
        // when
        postService.editPostById(post.getId(), postUpdateDto);

        // then
        Post changedPost = postRepository.findById(post.getId()).get();

        assertThat(changedPost.getTitle()).isEqualTo(updateTitle);
        assertThat(changedPost.getContent()).isEqualTo(updateContent);
    }

    @Test
    @DisplayName("[성공] PostService, editPostById() 실행하면 글이 수정되며 null인 항목은 수정되지 않는다")
    void editPostByIdNullValueSuccessTest() throws Exception {
        // given
        RequestPostCreateDto requestPostCreateDto = RequestPostCreateDto
                                                        .builder()
                                                        .title("title")
                                                        .content("content")
                                                        .build();
        Post post = Post.createPost(requestPostCreateDto);
        postRepository.save(post);

        String updateTitle = null;
        String updateContent = "update_content";
        RequestPostUpdateDto postUpdateDto = RequestPostUpdateDto
                                                        .builder()
                                                        .title(updateTitle)
                                                        .content(updateContent)
                                                        .build();
        // when
        postService.editPostById(post.getId(), postUpdateDto);

        // then
        Post changedPost = postRepository.findById(post.getId()).get();

        assertThat(changedPost.getTitle()).isEqualTo("title");
        assertThat(changedPost.getContent()).isEqualTo(updateContent);
    }

    @Test
    @DisplayName("[성공] PostService, editPostById() 실행하면 수정하려는 조회되지않고 NotFoundPostException 발생")
    void editPostByIdFailTest() throws Exception {
        // given
        RequestPostCreateDto requestPostCreateDto = RequestPostCreateDto
                                                            .builder()
                                                            .title("title")
                                                            .content("content")
                                                            .build();
        Post post = Post.createPost(requestPostCreateDto);
        postRepository.save(post);

        String updateTitle = "update_title";
        String updateContent = "update_content";
        RequestPostUpdateDto postUpdateDto = RequestPostUpdateDto
                                                            .builder()
                                                            .title(updateTitle)
                                                            .content(updateContent)
                                                            .build();
        // when & then
        assertThatThrownBy(() -> postService.editPostById(post.getId()+1L, postUpdateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(NOT_FOUND_POST.getMessage());
    }

    @Test
    @DisplayName("[성공] PostService, removePostById() 실행하면 글이 삭제된다.")
    void removePostByIdSuccessTest() throws Exception {
        // given
        RequestPostCreateDto requestPostCreateDto = RequestPostCreateDto
                                                                    .builder()
                                                                    .title("title")
                                                                    .content("content")
                                                                    .build();
        Post post = Post.createPost(requestPostCreateDto);
        postRepository.save(post);

        // when
        postService.removePostById(post.getId());

        // then
        assertThat(postRepository.findAll().size()).isEqualTo(0);
        assertThatThrownBy(() -> postService.selectPostById(post.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(NOT_FOUND_POST.getMessage());
    }

    @Test
    @DisplayName("[성공] PostService, removePostById() 실행하면 삭제하려는 조회되지않고 NotFoundPostException 발생")
    void removePostByIdFailTest() throws Exception {
        // given
        RequestPostCreateDto requestPostCreateDto = RequestPostCreateDto
                                                                    .builder()
                                                                    .title("title")
                                                                    .content("content")
                                                                    .build();
        Post post = Post.createPost(requestPostCreateDto);
        postRepository.save(post);

        // when & then
        assertThatThrownBy(() -> postService.removePostById(post.getId() + 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(NOT_FOUND_POST.getMessage());
    }

}