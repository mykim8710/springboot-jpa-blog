package com.mykim.blog.post.api;

import com.mykim.blog.global.pagination.CustomPaginationRequest;
import com.mykim.blog.global.pagination.CustomSortingRequest;
import com.mykim.blog.global.response.CommonResult;
import com.mykim.blog.global.response.SuccessCode;
import com.mykim.blog.post.dto.request.RequestPostCreateDto;
import com.mykim.blog.post.dto.request.RequestPostUpdateDto;
import com.mykim.blog.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PostApiController {

    private final PostService postService;

    /**
     * POST /api/v1/posts => 글 등록
     */
    @PostMapping("/api/v1/posts")
    public ResponseEntity<CommonResult> createPostApi(@RequestBody @Valid RequestPostCreateDto dto) {
        log.info("[POST] /api/v1/posts  =>  글 등록");
        postService.createPost(dto);
        return ResponseEntity
                        .ok()
                        .body(new CommonResult(SuccessCode.INSERT));
    }

    /**
     * GET /api/v1/posts/{postId} => 글 조회(단건조회)
     */
    @GetMapping("/api/v1/posts/{postId}")
    public ResponseEntity<CommonResult> selectPostByIdApi(@PathVariable Long postId) {
        log.info("[GET] /api/v1/posts/{postId}  =>  글 조회(단건조회)", postId);
        return ResponseEntity
                .ok()
                .body(new CommonResult(SuccessCode.COMMON, postService.selectPostById(postId)));
    }

    /**
     * GET /api/v1/posts => 글 목록조회(전체)
     */
    @GetMapping("/api/v1/posts")
    public ResponseEntity<CommonResult> selectPostAllApi() {
        log.info("[GET] /api/v1/posts  =>  글 목록조회(전체)");
         return ResponseEntity
                .ok()
                .body(new CommonResult(SuccessCode.COMMON, postService.selectPostAll()));
    }

    /**
     * GET /api/v2/posts => 글 목록조회(페이징 + 정렬)
     */
    @GetMapping("/api/v2/posts")
    public ResponseEntity<CommonResult> selectPostAllPaginationApi(@PageableDefault Pageable pageable) {
        log.info("[GET] /api/v2/posts  =>  글 목록조회(페이징 + 정렬)");
        log.info("pageable = {}" , pageable);

        return ResponseEntity
                .ok()
                .body(new CommonResult(SuccessCode.COMMON, postService.selectPostAllPagination(pageable)));
    }


    /**
     * GET /api/v3/posts => 글 목록조회(페이징 + 정렬 + 검색), Querydsl
     */
    @GetMapping("/api/v3/posts")
    public ResponseEntity<CommonResult> selectPostAllPaginationQuerydslApi(CustomPaginationRequest paginationRequest, CustomSortingRequest sortingRequest, String keyword) {
        log.info("[GET] /api/v3/posts  =>  글 목록조회(페이징 + 정렬 + 검색, Querydsl)");
        log.info("CustomPaginationRequest = {}", paginationRequest);
        log.info("CustomSortingRequest = {}", sortingRequest);
        log.info("keyword = {}", keyword);

        return ResponseEntity
                .ok()
                .body(new CommonResult(SuccessCode.COMMON, postService.selectPostAllPaginationQuerydsl(paginationRequest, sortingRequest, keyword)));
    }

    /**
     * PATCH  /api/v1/posts/{postId}  => 글 수정
     */
    @PatchMapping("/api/v1/posts/{postId}")
    public ResponseEntity<CommonResult> editPostByIdApi(@PathVariable Long postId, @RequestBody @Valid RequestPostUpdateDto dto) {
        log.info("[PATCH] /api/v1/posts/{}  =>  글 수정", postId);
        log.info("RequestPostUpdateDto = {]", dto);
        postService.editPostById(postId, dto);
        return ResponseEntity
                .ok()
                .body(new CommonResult(SuccessCode.UPDATE));
    }


    /**
     * DELETE  /api/v1/posts/{postId}  => 글 수정
     */
    @DeleteMapping("/api/v1/posts/{postId}")
    public ResponseEntity<CommonResult> removePostByIdApi(@PathVariable Long postId) {
        log.info("[DELETE] /api/v1/posts/{}  =>  글 삭제", postId);
        postService.removePostById(postId);
        return ResponseEntity
                .ok()
                .body(new CommonResult(SuccessCode.DELETE));
    }

}

