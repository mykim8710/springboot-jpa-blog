package com.mykim.blog.post.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mykim.blog.global.result.error.ErrorCode;
import com.mykim.blog.global.result.SuccessCode;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.domain.MemberRole;
import com.mykim.blog.member.dto.request.RequestMemberSignInDto;
import com.mykim.blog.member.repository.MemberRepository;
import com.mykim.blog.post.domain.Post;
import com.mykim.blog.post.dto.request.RequestPostCreateDto;
import com.mykim.blog.post.dto.request.RequestPostUpdateDto;
import com.mykim.blog.post.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.mykim.blog.global.result.SuccessCode.SIGN_IN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Slf4j
@AutoConfigureMockMvc // @SpringBootTest에서 MockMvc 객체를 사용하기 위함
@SpringBootTest
@Transactional
class PostApiControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("[성공] 인증필요없음  /api/v1/posts POST 요청 시 글등록이 된다.")
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
    @DisplayName("[실패] 인증필요없음  /api/v1/posts POST 요청 시 제목, 내용 값이 필수이므로 글등록이 실패한다.")
    void createPostApiFailTest() throws Exception {
        // given
        String api = "/api/v1/posts";
        RequestPostCreateDto requestPostCreateDto = RequestPostCreateDto
                                                                .builder()
                                                                .title("")
                                                                .content(null)
                                                                .build();
        String requestDtoJsonStr = objectMapper.writeValueAsString(requestPostCreateDto);

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
                .andExpect(jsonPath("$.data.length()", Matchers.is(2)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("[성공] 인증필요없음  /api/v1/posts/{postId} GET 요청 시 글 하나가 조회된다.")
    void selectPostByIdApiSuccessTest() throws Exception {
        // given
        String title = "title";
        String content = "content";

        Post post = Post.builder()
                            .title(title)
                            .content(content)
                            .build();

        Post savePost = postRepository.save(post);

        String api = "/api/v1/posts/{postId}";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api, savePost.getId())
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
    @DisplayName("[실패] 인증필요없음  /api/v1/posts/{postId} GET 요청 시 글 하나가 조회되지 않고 NotFoundPostException이 발생한다.(존재하지 않는 글)")
    void selectPostByIdApiFailTest() throws Exception {
        // given
        String api = "/api/v1/posts/{postId}";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api, -1)
                        .contentType(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.status").value(ErrorCode.NOT_FOUND_POST.getStatus()))
                .andExpect(jsonPath("$.code").value(ErrorCode.NOT_FOUND_POST.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_POST.getMessage()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("[성공] 인증필요없음  /api/v1/posts/all GET 요청 시 글 전체가 조회된다.")
    void selectPostAllApiSuccessTest() throws Exception {
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

        String api = "/api/v1/posts/all";

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
    @DisplayName("[성공] 인증필요없음  /api/v1/posts/all-pagination GET 요청 시 선택한 페이지의 글이 조회된다.")
    void selectPostAllPaginationApiSuccessTest() throws Exception {
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


        String api = "/api/v1/posts/all-pagination";

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

    @Test
    @DisplayName("[성공] 인증필요없음  /api/v1/posts GET 요청 시 선택한 페이지의 글이 조회된다.")
    void selectPostAllPaginationQuerydslApiSuccessTest() throws Exception {
        // given
        /**
         * 이 코드를 람다식을 사용해서 아래와 같이 사용할 수 있다.
         for (int i = 0; i < 30>; i++) {
         Post post = Post.builder().title("title_"+i).content("content_"+i).build();
         createdPosts.add(post);
         }
         **/
        Member member = createMember("aaa", "1111");

        List<Post> createdPosts = IntStream.range(1, 31)
                                            .mapToObj(i -> Post.builder()
                                                    .title("title_" +i)
                                                    .content("content_" +i)
                                                    .member(member)
                                                    .build()
                                            ).collect(Collectors.toList());

        postRepository.saveAll(createdPosts);


        String api = "/api/v1/posts";

        // sql limit, offset, sort, search
        int page = 2;
        int size = 10;
        String sort = "id,desc";
        String keyword = "";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api)
                        .queryParam("page", String.valueOf(page))
                        .queryParam("size", String.valueOf(size))
                        .queryParam("sort", sort)
                        .queryParam("keyword", keyword)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.COMMON.getStatus()))
                .andExpect(jsonPath("$.code").value(SuccessCode.COMMON.getCode()))
                .andExpect(jsonPath("$.message").value(SuccessCode.COMMON.getMessage()))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.content.length()", Matchers.is(size)))
                .andExpect(jsonPath("$.data.content[0].title").value("title_20"))
                .andExpect(jsonPath("$.data.content[4].title").value("title_16"))
                .andExpect(jsonPath("$.data.content[9].title").value("title_11"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("[성공] 인증필요없음  /api/v1/posts/{postId} PATCH 요청 시 글이 수정된다.")
    void editPostByIdApiSuccessTest() throws Exception {
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

        String postUpdateDtoJsonStr = objectMapper.writeValueAsString(postUpdateDto);
        String api = "/api/v1/posts/{postId}";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.patch(api, post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(postUpdateDtoJsonStr)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.UPDATE.getStatus()))
                .andExpect(jsonPath("$.code").value(SuccessCode.UPDATE.getCode()))
                .andExpect(jsonPath("$.message").value(SuccessCode.UPDATE.getMessage()))
                .andDo(MockMvcResultHandlers.print());


        Post findPost = postRepository.findById(post.getId()).get();
        assertThat(findPost.getTitle()).isEqualTo(updateTitle);
        assertThat(findPost.getContent()).isEqualTo(updateContent);
    }

    @Test
    @DisplayName("[실패] 인증필요없음  /api/v1/posts/{postId} PATCH 요청 시 글 하나가 수정되지 않고 NotFoundPostException이 발생한다.(존재하지 않는 글)")
    void editPostByIdApiFailTest() throws Exception {
        // given
        String updateTitle = "update_title";
        String updateContent = "update_content";
        RequestPostUpdateDto postUpdateDto = RequestPostUpdateDto
                                                        .builder()
                                                        .title(updateTitle)
                                                        .content(updateContent)
                                                        .build();

        String postUpdateDtoJsonStr = objectMapper.writeValueAsString(postUpdateDto);
        String api = "/api/v1/posts/{postId}";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.patch(api, -1)
                        .contentType(APPLICATION_JSON)
                        .content(postUpdateDtoJsonStr))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.status").value(ErrorCode.NOT_FOUND_POST.getStatus()))
                .andExpect(jsonPath("$.code").value(ErrorCode.NOT_FOUND_POST.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_POST.getMessage()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("[성공] 인증필요없음  /api/v1/posts/{postId} DELETE 요청 시 글이 삭제된다.")
    void removePostByIdApiSuccessTest() throws Exception {
        // given
        RequestPostCreateDto requestPostCreateDto = RequestPostCreateDto
                                                                .builder()
                                                                .title("title")
                                                                .content("content")
                                                                .build();
        Post post = Post.createPost(requestPostCreateDto);
        postRepository.save(post);

        String api = "/api/v1/posts/{postId}";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete(api,  post.getId())
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.DELETE.getStatus()))
                .andExpect(jsonPath("$.code").value(SuccessCode.DELETE.getCode()))
                .andExpect(jsonPath("$.message").value(SuccessCode.DELETE.getMessage()))
                .andDo(MockMvcResultHandlers.print());

        Optional<Post> optionalFindPost = postRepository.findById(post.getId());
        assertThat(optionalFindPost).isEmpty();

    }

    @Test
    @DisplayName("[실패] 인증필요없음  /api/v1/posts/{postId} DELETE 요청 시 글 하나가 삭제되지 않고 실패한다.(존재하지 않는 글)")
    void removePostByIdApiFailTest() throws Exception {
        // given
        String api = "/api/v1/posts/{postId}";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete(api, -1)
                        .contentType(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.status").value(ErrorCode.NOT_FOUND_POST.getStatus()))
                .andExpect(jsonPath("$.code").value(ErrorCode.NOT_FOUND_POST.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_POST.getMessage()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());
    }


    /** ================================================ **/


    @Test
    @DisplayName("[성공] 신원인증(로그인) 완료 후 /api/v2/posts POST 요청 시 글등록이 된다.")
    void createPostV2ApiSuccessTest() throws Exception {
        // given

        // create member
        String email = "abc@abc.com";
        String password = "1111";
        Member member = createMember(email, password);

        // sign-in
        String jwt = memberSignInReturnJwt(email, password);

        // Post Create Dto
        RequestPostCreateDto requestPostCreateDto = RequestPostCreateDto
                                                                .builder()
                                                                .title("title")
                                                                .content("content")
                                                                .build();

        String requestPostDtoJsonStr = objectMapper.writeValueAsString(requestPostCreateDto);
        String createPostApi = "/api/v2/posts";

        // when
        mockMvc.perform(MockMvcRequestBuilders.post(createPostApi)
                                            .header("Authorization", jwt)
                                            .contentType(APPLICATION_JSON)
                                            .content(requestPostDtoJsonStr)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.INSERT.getStatus()))
                .andExpect(jsonPath("$.code").value(SuccessCode.INSERT.getCode()))
                .andExpect(jsonPath("$.message").value(SuccessCode.INSERT.getMessage()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print());

        // then
        assertThat(postRepository.count()).isEqualTo(1);
        Post findPost = postRepository.findAll().get(0);
        assertThat(requestPostCreateDto.getTitle()).isEqualTo(findPost.getTitle());
        assertThat(requestPostCreateDto.getContent()).isEqualTo(findPost.getContent());
    }

    @Test
    @DisplayName("[성공] 신원인증(로그인) 완료 후 /api/v2/posts/{postId} GET 요청 시 글 하나가 조회된다.")
    void selectPostByIdV2ApiSuccessTest() throws Exception {
        // given

        // create member
        String email = "abc@abc.com";
        String password = "1111";
        Member member = createMember(email, password);

        // create post
        Post post = createPostOne(member);

        // sign-in
        String jwt = memberSignInReturnJwt(email, password);

        String api = "/api/v2/posts/{postId}";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api, post.getId())
                        .header("Authorization", jwt)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.COMMON.getStatus()))
                .andExpect(jsonPath("$.code").value(SuccessCode.COMMON.getCode()))
                .andExpect(jsonPath("$.message").value(SuccessCode.COMMON.getMessage()))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.title").value(post.getTitle()))
                .andExpect(jsonPath("$.data.content").value(post.getContent()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("[성공] 신원인증(로그인) 완료 후 /api/v2/posts GET 요청 시 선택한 페이지의 글이 조회된다.")
    void selectPostAllPaginationQuerydslV2ApiSuccessTest() throws Exception {
        // given

        // create Member
        String email = "abc@abc.com";
        String password = "1111";
        Member member = createMember(email, password);

        String email2 = "qwerty@qwerty.com";
        String password2 = "1111";
        Member member2 = createMember(email2, password2);

        // create post
        createPostList(1, 11, member);
        createPostList(11, 21, member2);


        // sign-in : member1
        String jwt = memberSignInReturnJwt(email, password);


        // get Post List
        String api = "/api/v2/posts";

        // sql limit, offset, sort, search
        int page = 1;
        int size = 5;
        String sort = "id,desc";
        String keyword = "";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(api)
                        .header("Authorization", jwt)
                        .queryParam("page", String.valueOf(page))
                        .queryParam("size", String.valueOf(size))
                        .queryParam("sort", sort)
                        .queryParam("keyword", keyword)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.COMMON.getStatus()))
                .andExpect(jsonPath("$.code").value(SuccessCode.COMMON.getCode()))
                .andExpect(jsonPath("$.message").value(SuccessCode.COMMON.getMessage()))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.responsePostSelectDtos.length()", Matchers.is(size)))
                .andExpect(jsonPath("$.data.responsePostSelectDtos[0].title").value("title_10"))
                .andExpect(jsonPath("$.data.responsePostSelectDtos[4].title").value("title_6"))
                .andExpect(jsonPath("$.data.paginationResponse.page").value(page))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("[성공] 신원인증(로그인) 완료 후 /api/v2/posts/{postId} PATCH 요청 시 글이 수정된다.")
    void editPostByIdV2ApiSuccessTest() throws Exception {
        // given

        // create member
        String email = "abc@abc.com";
        String password = "1111";

        Member member = createMember(email, password);

        // create post
        Post createdPost = createPostOne(member);

        // sign-in : member
        String jwt = memberSignInReturnJwt(email, password);


        // update post
        String updateTitle = "update_title";
        String updateContent = "update_content";
        RequestPostUpdateDto postUpdateDto = RequestPostUpdateDto
                                                        .builder()
                                                        .title(updateTitle)
                                                        .content(updateContent)
                                                        .build();

        String postUpdateDtoJsonStr = objectMapper.writeValueAsString(postUpdateDto);
        String api = "/api/v2/posts/{postId}";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.patch(api, createdPost.getId())
                        .header("Authorization", jwt)
                        .contentType(APPLICATION_JSON)
                        .content(postUpdateDtoJsonStr)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.UPDATE.getStatus()))
                .andExpect(jsonPath("$.code").value(SuccessCode.UPDATE.getCode()))
                .andExpect(jsonPath("$.message").value(SuccessCode.UPDATE.getMessage()))
                .andDo(MockMvcResultHandlers.print());


        Post findPost = postRepository.findById(createdPost.getId()).get();
        assertThat(findPost.getTitle()).isEqualTo(updateTitle);
        assertThat(findPost.getContent()).isEqualTo(updateContent);
    }

    @Test
    @DisplayName("[성공] 신원인증(로그인) 완료 후 /api/v2/posts/{postId} DELETE 요청 시 글이 삭제된다.")
    void removePostByIdV2ApiSuccessTest() throws Exception {
        // given

        // create member
        String email = "abc@abc.com";
        String password = "1111";

        Member member = createMember(email, password);

        // create post
        Post post = createPostOne(member);

        // sign-in : member1
        String jwt = memberSignInReturnJwt(email, password);


        String api = "/api/v2/posts/{postId}";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete(api,  post.getId())
                        .header("Authorization", jwt)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.DELETE.getStatus()))
                .andExpect(jsonPath("$.code").value(SuccessCode.DELETE.getCode()))
                .andExpect(jsonPath("$.message").value(SuccessCode.DELETE.getMessage()))
                .andDo(MockMvcResultHandlers.print());

        Optional<Post> optionalFindPost = postRepository.findById(post.getId());
        assertThat(optionalFindPost).isEmpty();
    }

    private String memberSignInReturnJwt(String email, String password) throws Exception {
        String signInApi = "/sign-in";
        RequestMemberSignInDto requestMemberSignInDto = RequestMemberSignInDto.builder()
                .email(email)
                .password(password)
                .build();

        String requestDtoJsonStr = objectMapper.writeValueAsString(requestMemberSignInDto);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(signInApi)
                        .contentType(APPLICATION_JSON)
                        .content(requestDtoJsonStr)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String jwt = response.getHeader("Authorization");
        log.info("jwt : {}", jwt);
        return jwt;
    }
    private Member createMember(String email, String password) {
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .username("abc")
                .memberRole(MemberRole.ROLE_MEMBER)
                .build();

        memberRepository.save(member);

        return member;
    }
    private void createPostList(int start, int last, Member member) {
        List<Post> createdPosts = IntStream.range(start, last)
                .mapToObj(i -> Post.builder()
                        .title("title_" +i)
                        .content("content_" +i)
                        .member(member)
                        .build()
                ).collect(Collectors.toList());

        postRepository.saveAll(createdPosts);
    }
    private Post createPostOne(Member member) {
        RequestPostCreateDto requestPostCreateDto = RequestPostCreateDto.builder()
                .title("title")
                .content("content")
                .build();
        Post post = Post.createPost(requestPostCreateDto, member);
        postRepository.save(post);

        return post;
    }

}