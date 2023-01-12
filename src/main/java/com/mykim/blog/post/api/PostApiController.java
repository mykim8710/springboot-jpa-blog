package com.mykim.blog.post.api;

import com.mykim.blog.global.response.CommonResult;
import com.mykim.blog.global.response.SuccessCode;
import com.mykim.blog.post.dto.request.RequestPostCreateDto;
import com.mykim.blog.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * GET /api/v1/posts/{postId} => 글 하나 조회
     */
    @GetMapping("/api/v1/posts/{postId}")
    public ResponseEntity<CommonResult> selectPostByIdApi(@PathVariable Long postId) {
        log.info("[GET] /api/v1/posts/{postId}  =>  글 하나 조회", postId);
        return ResponseEntity
                .ok()
                .body(new CommonResult(SuccessCode.COMMON,postService.selectPostById(postId)));
    }

    /**
     * GET /api/v1/posts => 글 전체조회(검색 + 페이징)
     */
    @GetMapping("/api/v1/posts")
    public ResponseEntity<CommonResult> selectPostAllApi() {
        log.info("[GET] /api/v1/posts  =>  글 전체조회(검색 + 페이징)");



        return ResponseEntity
                .ok()
                .body(new CommonResult(SuccessCode.COMMON));
    }







}

