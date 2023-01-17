package com.mykim.blog.member.api;

import com.mykim.blog.global.config.authorization.AuthorizationMember;
import com.mykim.blog.global.config.authorization.annotation.CustomAuthorization;
import com.mykim.blog.global.response.CommonResult;
import com.mykim.blog.member.dto.request.RequestMemberSignInDto;
import com.mykim.blog.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.mykim.blog.global.response.SuccessCode.SIGN_IN;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthApiController {

    private final AuthService authService;

    @GetMapping("/api/v1/auth/foo")
    public String fooAuthTestApi(@CustomAuthorization AuthorizationMember signInMember) {
        log.info("[GET] /api/auth/foo  {}");
        log.info("signInMember = {}" ,signInMember);
        return "인증이 필요한 api : 인증성공";
    }

    @GetMapping("/api/v1/auth/bar")
    public String barAuthTestApi() {
        log.info("[GET] /api/auth/bar");
        return "인증이 필요없는 페이지";
    }


    /**
     * POST /api/v1/auth/sign-in  =>  로그인
     */
    @PostMapping("/api/v1/auth/sign-in")
    public ResponseEntity<CommonResult> signInMemberApi(@RequestBody @Valid RequestMemberSignInDto signInDto, HttpServletResponse response) {
        log.info("[POST] /api/v1/auth/sign-in");
        log.info("RequestMemberSignInDto = {}", signInDto);

        authService.signInMember(signInDto, response);

        return ResponseEntity.ok()
                .body(new CommonResult(SIGN_IN));
    }




}
