package com.mykim.blog.auth.manual.api;

import com.mykim.blog.auth.manual.annotation.CustomJwtAuthorization;
import com.mykim.blog.auth.manual.annotation.CustomSessionAuthorization;
import com.mykim.blog.auth.manual.dto.request.RequestAuthDto;
import com.mykim.blog.auth.manual.dto.response.ResponseAuthDto;
import com.mykim.blog.auth.manual.service.JwtAuthService;
import com.mykim.blog.auth.manual.service.SessionAuthService;
import com.mykim.blog.global.result.CommonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;

import static com.mykim.blog.global.result.SuccessCode.SIGN_IN;
import static com.mykim.blog.global.result.SuccessCode.SIGN_OUT;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthApiController {
    private final SessionAuthService sessionAuthService;
    private final JwtAuthService jwtAuthService;


    @GetMapping("/api/v1/auth/foo")
    public String fooAuthorizationTestApi(@CustomSessionAuthorization ResponseAuthDto authDto) {
        log.info("[GET] /api/v1/auth/foo  {}");
        log.info("authorizationMemberDto = {}" ,authDto);
        return "인증이 필요한 api : 인증성공";
    }

    @GetMapping("/api/v2/auth/foo")
    public String fooAuthorizationWithJwtTestApi(@CustomJwtAuthorization ResponseAuthDto authDto) {
        log.info("[GET] /api/v2/auth/foo  {}");
        log.info("authorizationMemberDto = {}" ,authDto);
        return "jwt 인증이 필요한 api : 인증성공";
    }

    @GetMapping("/api/v1/auth/bar")
    public String barAuthorizationTestApi() {
        log.info("[GET] /api/v1/auth/bar");
        return "인증이 필요없는 api";
    }


    /**
     * POST /api/v1/auth/sign-in  =>  로그인
     */
    @PostMapping("/api/v1/auth/sign-in")
    public ResponseEntity<CommonResult> signInApi(@RequestBody @Valid RequestAuthDto authDto, HttpServletResponse response) {
        log.info("[POST] /api/v1/auth/sign-in");
        log.info("RequestAuthDto = {}", authDto);

        // 인증 후 access token 발급
        String accessToken = sessionAuthService.authenticate(authDto);

        // 쿠키에 access token 저장
        Cookie cookie = new Cookie("AUTHORIZATION_SESSION", accessToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(3600*24*30); // 30 days default
        response.addCookie(cookie);

        return ResponseEntity.ok()
                .body(new CommonResult(SIGN_IN));
    }

    /**
     * GET /api/v1/auth/sign-out  =>  로그아웃
     */
    @GetMapping("/api/v1/auth/sign-out")
    public ResponseEntity<CommonResult> signOutApi(HttpServletRequest request, HttpServletResponse response) {
        log.info("[POST] /api/v1/auth/sign-out");

        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            Cookie authCookie = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("AUTHORIZATION_SESSION"))
                    .findFirst()
                    .orElse(null);

            if(authCookie != null) {
                sessionAuthService.expireAuthSession(authCookie.getValue());
            }
        }

        // 쿠키 초기화 : delete cookie
        Cookie cookie = new Cookie("AUTHORIZATION_SESSION", "");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok()
                            .body(new CommonResult(SIGN_OUT));
    }

    /**
     * POST /api/v2/auth/sign-in  =>  로그인, jwt
     */
    @PostMapping("/api/v2/auth/sign-in")
    public ResponseEntity<CommonResult> signInWithJwtApi(@RequestBody @Valid RequestAuthDto authDto, HttpServletRequest request, HttpServletResponse response) {
        log.info("[POST] /api/v2/auth/sign-in");
        log.info("RequestAuthDto = {}", authDto);

        String jwtToken = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length != 0) {
            Cookie jwtCookie = Arrays.stream(cookies)
                                        .filter(cookie -> cookie.getName().equals("AUTHORIZATION_JWT"))
                                        .findFirst()
                                        .orElse(null);

            if(jwtCookie != null) {
                jwtToken = jwtCookie.getValue();
            }
        }

        final String jwt = jwtAuthService.authenticateJwt(authDto, jwtToken);
        Cookie cookie = new Cookie("AUTHORIZATION_JWT", jwt);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(3600*24*30); // 30 days default
        response.addCookie(cookie);

        return ResponseEntity.ok()
                //.header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new CommonResult(SIGN_IN));
    }

    /**
     * GET /api/v2/auth/sign-out  =>  로그아웃, jwt
     */
    @GetMapping("/api/v2/auth/sign-out")
    public ResponseEntity<CommonResult> signOutWithJwtApi(HttpServletRequest request, HttpServletResponse response) {
        log.info("[POST] /api/v2/auth/sign-out");

        // 쿠키 초기화 : delete cookie
        Cookie cookie = new Cookie("AUTHORIZATION_JWT", "");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok()
                .body(new CommonResult(SIGN_OUT));
    }

}
