package com.mykim.blog.member.api;

import com.mykim.blog.auth.security.jwt.JwtProperties;
import com.mykim.blog.auth.security.jwt.JwtProvider;
import com.mykim.blog.global.result.CommonResult;
import com.mykim.blog.global.result.SuccessCode;
import com.mykim.blog.global.result.error.ErrorCode;
import com.mykim.blog.member.dto.request.RequestMemberInsertDto;
import com.mykim.blog.member.exception.UnAuthorizedMemberException;
import com.mykim.blog.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberApiController {
    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    /**
     * POST /api/v1/members/sign-up  =>  회원가입
     */
    @PostMapping("/api/v1/members/sign-up")
    public ResponseEntity<CommonResult> signUpMemberApi(@RequestBody @Valid RequestMemberInsertDto memberInsertDto) {
        log.info("[POST] /api/v1/members/sign-up  =>  create member(== sign up)");
        log.info("RequestMemberInsertDto = {}", memberInsertDto);
        memberService.signUpMember(memberInsertDto);
        return ResponseEntity.ok()
                .body(new CommonResult(SuccessCode.INSERT));
    }


    @GetMapping("/sign-out")
    public ResponseEntity<CommonResult> signOutMemberApi(HttpServletRequest request) {
        log.info("[GET] /sign-out");
        memberService.expireJwt(request);
        return ResponseEntity.ok()
                .body(new CommonResult(SuccessCode.SIGN_OUT));
    }

}
