package com.mykim.blog.global.authorization.api;

import com.mykim.blog.global.authorization.annotation.CustomAuthorization;
import com.mykim.blog.global.authorization.dto.request.RequestAuthorizationDto;
import com.mykim.blog.global.authorization.dto.response.ResponseAuthorizationDto;
import com.mykim.blog.global.authorization.service.AuthorizationService;
import com.mykim.blog.global.response.CommonResult;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.security.Key;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;

import static com.mykim.blog.global.response.SuccessCode.SIGN_IN;
import static com.mykim.blog.global.response.SuccessCode.SIGN_OUT;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthorizationApiController {
    private final AuthorizationService authorizationService;
    private static final String KEY = "jiS48dIGRtD73A/st4gd8SxL7AHSdkPtNb7oO9p22rI=";

    @GetMapping("/api/v1/auth/foo")
    public String fooAuthorizationTestApi(@CustomAuthorization ResponseAuthorizationDto authorizationDto) {
        log.info("[GET] /api/v1/auth/foo  {}");
        log.info("authorizationMemberDto = {}" ,authorizationDto);
        return "인증이 필요한 api : 인증성공";
    }

    @GetMapping("/api/v2/auth/foo")
    public String fooAuthorizationWithJwtTestApi(@CustomAuthorization ResponseAuthorizationDto authorizationDto) {
        log.info("[GET] /api/v2/auth/foo  {}");
        log.info("authorizationMemberDto = {}" ,authorizationDto);
        return "인증이 필요한 api : 인증성공";
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
    public ResponseEntity<CommonResult> signInApi(@RequestBody @Valid RequestAuthorizationDto authorizationDto) {
        log.info("[POST] /api/v1/auth/sign-in");
        log.info("RequestAuthorizationDto = {}", authorizationDto);

        // 인증 후 access token 발급
        String accessToken = authorizationService.authenticate(authorizationDto);

        ResponseCookie responseCookie = ResponseCookie
                                                .from("AUTHORIZATION_SESSION", accessToken)
                                                .domain("localhost") // todo 서버환경에 따른 분리 필요
                                                .path("/api/v1/auth/sign-in")
                                                .httpOnly(true)
                                                .secure(false)
                                                .maxAge(Duration.ofDays(1)) // 쿠키 만료시간, 1day == 24h = 1440m == 86400s
                                                .sameSite("Strict")
                                                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new CommonResult(SIGN_IN));
    }

    /**
     * GET /api/v1/auth/sign-out  =>  로그아웃
     */
    @GetMapping("/api/v1/auth/sign-out")
    public ResponseEntity<CommonResult> signOutApi(HttpServletRequest request) {
        log.info("[POST] /api/v1/auth/sign-out");

        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            Cookie authCookie = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("AUTHORIZATION_SESSION"))
                    .findFirst()
                    .orElse(null);

            if(authCookie != null) {
                authorizationService.expireAuthorizationSession(authCookie.getValue());
            }
        }

        return ResponseEntity.ok()
                            .body(new CommonResult(SIGN_OUT));
    }

    /**
     * POST /api/v2/auth/sign-in  =>  로그인, jwt
     */
    @PostMapping("/api/v2/auth/sign-in")
    public String signInWithJwtApi(@RequestBody @Valid RequestAuthorizationDto authorizationDto) {
        log.info("[POST] /api/v2/auth/sign-in");
        log.info("RequestAuthorizationDto = {}", authorizationDto);


        // We need a signing key, so we'll create one just for this example.
        // Usually the key would be read from your application configuration instead.
        //Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        /*byte[] encodedKey = key.getEncoded();
        String strKey = Base64.getEncoder().encodeToString(encodedKey);
        System.out.println("strKey = " + strKey);   //jiS48dIGRtD73A/st4gd8SxL7AHSdkPtNb7oO9p22rI=*/

        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(KEY));

        String jws = Jwts.builder()
                            .setSubject("Joe")
                            .signWith(key)
                            .compact();

        System.out.println("jws = " + jws);

        return jws;
    }

}
