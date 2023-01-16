package com.mykim.blog.member.api;

import com.mykim.blog.global.response.CommonResult;
import com.mykim.blog.global.response.SuccessCode;
import com.mykim.blog.member.dto.request.RequestMemberInsertDto;
import com.mykim.blog.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberApiController {

    private final MemberService memberService;

    /**
     * POST /api/v1/members  =>  회원가입
     */
    @PostMapping("/api/v1/members")
    public ResponseEntity<CommonResult> createMemberApi(@RequestBody @Valid RequestMemberInsertDto memberInsertDto) {
        log.info("[POST] /api/v1/members  =>  create member(== sign up)");
        log.info("RequestMemberInsertDto = {}", memberInsertDto);

        memberService.createMember(memberInsertDto);

        return ResponseEntity.ok()
                .body(new CommonResult(SuccessCode.INSERT));
    }




}
