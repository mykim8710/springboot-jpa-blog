package com.mykim.blog.post.api;

import com.mykim.blog.auth.security.principal.PrincipalDetail;
import com.mykim.blog.global.pagination.CustomPaginationRequest;
import com.mykim.blog.global.pagination.CustomSortingRequest;
import com.mykim.blog.global.result.CommonResult;
import com.mykim.blog.global.result.SuccessCode;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.post.dto.request.RequestPostCreateDto;
import com.mykim.blog.post.dto.request.RequestPostUpdateDto;
import com.mykim.blog.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
     * GET /api/v1/posts/all => 글 목록조회(전체), not used
     */
    @GetMapping("/api/v1/posts/all")
    public ResponseEntity<CommonResult> selectPostAllApi() {
        log.info("[GET] /api/v1/posts/all  =>  글 목록조회(전체)");
        return ResponseEntity
                .ok()
                .body(new CommonResult(SuccessCode.COMMON, postService.selectPostAll()));
    }

    /**
     * GET /api/v1/posts/all-pagination => 글 목록조회(페이징 + 정렬), not used
     */
    @GetMapping("/api/v1/posts/all-pagination")
    public ResponseEntity<CommonResult> selectPostAllPaginationApi(@PageableDefault Pageable pageable) {
        log.info("[GET] /api/v1/posts/all-pagination  =>  글 목록조회(페이징 + 정렬)");
        log.info("pageable = {}", pageable);

        return ResponseEntity
                .ok()
                .body(new CommonResult(SuccessCode.COMMON, postService.selectPostAllPagination(pageable)));
    }

    /**
     * GET /api/v1/posts => 글 목록조회(페이징 + 정렬 + 검색), Querydsl
     */
    @GetMapping("/api/v1/posts")
    public ResponseEntity<CommonResult> selectPostAllPaginationQuerydslApi(@ModelAttribute CustomPaginationRequest paginationRequest,
                                                                           @ModelAttribute CustomSortingRequest sortingRequest,
                                                                           @RequestParam String keyword) {
        log.info("[GET] /api/v1/posts  =>  글 목록조회(페이징 + 정렬 + 검색, Querydsl)");
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


    /** ====================================================================================================== */


    /**
     * POST /api/v2/posts => 글 등록
     */
    @PostMapping("/api/v2/posts")
    public ResponseEntity<CommonResult> createPostV2Api(@RequestBody @Valid RequestPostCreateDto postCreateDto, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        log.info("[POST] /api/v2/posts  =>  글 등록");

        Member member = principalDetail.getMember();
        System.out.println("member.getUsername() = " + member.getUsername());
        System.out.println("member.getEmail() = " + member.getEmail());
        System.out.println("member.getMemberRole() = " + member.getMemberRole());

        postService.createPostV2(postCreateDto, principalDetail.getMember());
        return ResponseEntity
                .ok()
                .body(new CommonResult(SuccessCode.INSERT));
    }

    /**
     * GET /api/v2/posts/{postId} => 글 조회(단건조회)
     */
    @GetMapping("/api/v2/posts/{postId}")
    public ResponseEntity<CommonResult> selectPostByIdV2Api(@PathVariable Long postId, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        log.info("[GET] /api/v2/posts/{postId}  =>  글 조회(단건조회)", postId);
        return ResponseEntity
                .ok()
                .body(new CommonResult(SuccessCode.COMMON, postService.selectPostByIdV2(postId, principalDetail.getMember().getId())));
    }

    /**
     * GET /api/v2/posts => 글 목록조회(페이징 + 정렬 + 검색), Querydsl
     */
    @GetMapping("/api/v2/posts")
    public ResponseEntity<CommonResult> selectPostAllPaginationQuerydslV4Api(@ModelAttribute CustomPaginationRequest paginationRequest,
                                                                             @ModelAttribute CustomSortingRequest sortingRequest,
                                                                             @RequestParam String keyword,
                                                                             @AuthenticationPrincipal PrincipalDetail principalDetail) {
        log.info("[GET] /api/v2/posts  =>  글 목록조회(페이징 + 정렬 + 검색, Querydsl)");
        log.info("CustomPaginationRequest = {}", paginationRequest);
        log.info("CustomSortingRequest = {}", sortingRequest);
        log.info("keyword = {}", keyword);

        return ResponseEntity
                .ok()
                .body(new CommonResult(SuccessCode.COMMON, postService.selectPostAllPaginationQuerydslV2(paginationRequest, sortingRequest, keyword, principalDetail.getMember().getId())));
    }

    /**
     * PATCH  /api/v2/posts/{postId}  => 글 수정
     */
    @PatchMapping("/api/v2/posts/{postId}")
    public ResponseEntity<CommonResult> editPostByIdV2Api(@PathVariable Long postId, @RequestBody @Valid RequestPostUpdateDto dto, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        log.info("[PATCH] /api/v2/posts/{}  =>  글 수정", postId);
        log.info("RequestPostUpdateDto = {]", dto);

        postService.editPostByIdV2(postId, dto, principalDetail.getMember().getId());
        return ResponseEntity
                .ok()
                .body(new CommonResult(SuccessCode.UPDATE));
    }

    /**
     * DELETE  /api/v2/posts/{postId}  => 글 삭제
     */
    @DeleteMapping("/api/v2/posts/{postId}")
    public ResponseEntity<CommonResult> removePostByIdV2Api(@PathVariable Long postId, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        log.info("[DELETE] /api/v2/posts/{}  =>  글 삭제", postId);
        postService.removePostByIdV2(postId, principalDetail.getMember().getId());
        return ResponseEntity
                .ok()
                .body(new CommonResult(SuccessCode.DELETE));
    }

}

