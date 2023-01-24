package com.mykim.blog.auth.security.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spring.security.jwt.dto.RequestUserSignInDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 스프링 시큐리티 : UsernamePasswordAuthenticationFilter
 * httpSecurity.formLogin()
 * [POST] /login [username, password] UsernamePasswordAuthenticationFilter
 *
 * 현재는 jwt방식을 사용하기 때문에 formLogin().disable() => UsernamePasswordAuthenticationFilter는 동작하지않음
 * UsernamePasswordAuthenticationFilter 상속받아 jwt 사용 시 로그인 용 필터를 구현 => security 에 등록
 */

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    /**
     *  [POST] /login 요청, 로그인 시도를 위해서 실행되는 함수
     *  {
     *      "username" : "",
     *      "password: : ""
     *  }
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("[POST] /sign-in, JwtAuthenticationFilter 실행");

        RequestUserSignInDto signInDto;

        try {
            // 1. request 객체로 부터 username, password get
            signInDto = new ObjectMapper().readValue(request.getInputStream(), RequestUserSignInDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // 2. signInDto로 UsernamePasswordAuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(signInDto.getUsername(), signInDto.getPassword());

        // authenticationManager로 인증시도를 하면 PrincipalDetailsService - loadUserByUsername()가 호출
        // 만약 AuthenticationProvider를 구현한 CustomAuthenticationProvider가 @Component롤 등록되어있다면 : 구현하지 않는다면 스프링 자체적으로 실행됨
        // CustomAuthenticationProvider의 authenticate() 메서드가 실행됨
        // AuthenticationManager -> AuthenticationProvider, authenticate() 메서드 실행
        AuthenticationManager authenticationManager = getAuthenticationManager();
        return authenticationManager.authenticate(authenticationToken);
    }

    // attemptAuthentication()실행 후 인증이 정상적으로 되었다면 successfulAuthentication()가 실행
    // 여기서 jwt 토큰을 발급하고 요청한 사용자에게 jwt 토큰을 response 해주면됨ㅣ
    // CustomAuthenticationSuccessHandler에서 해줘도됨
    /*@Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 => jwt 토큰 발급");
        PrincipalDetail principalDetail = (PrincipalDetail) authResult.getPrincipal();
        String jwt = JWT.create()
                                .withIssuer("issuer")   // payload - issuer : 토큰 발급자
                                .withSubject("userAuthJwtToken") // payload - subject : 토큰 제목
                                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME)) //  payload - expiration : 토큰 만료 시간
                                // 비공개 클래임
                                .withClaim("id", principalDetail.getUser().getId())
                                .withClaim("username", principalDetail.getUser().getUsername())
                                .sign(Algorithm.HMAC512(JwtProperties.SECRET_KEY));
        log.info("new jwt : {}", jwt);

        // set jwt token in header
        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX +jwt);


        // set json type response
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        MediaType jsonMimeType = MediaType.APPLICATION_JSON;

        HttpStatus result = HttpStatus.OK;
        CommonResult commonResult = new CommonResult(SuccessCode.SIGN_IN);

        if (jsonConverter.canWrite(result.getClass(), jsonMimeType)) {
            jsonConverter.write(commonResult, jsonMimeType, new ServletServerHttpResponse(response));
        }

    }*/
}
