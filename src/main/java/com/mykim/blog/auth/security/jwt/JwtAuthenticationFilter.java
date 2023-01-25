package com.mykim.blog.auth.security.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mykim.blog.member.dto.request.RequestMemberSignInDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
     *  [POST] /sign-in 요청, 로그인 시도를 위해서 실행되는 함수
     *  {
     *      "username" : "",
     *      "password: : ""
     *  }
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("[POST] /sign-in, JwtAuthenticationFilter 실행");

        RequestMemberSignInDto signInDto;

        try {
            // 1. request 객체로 부터 username, password get
            signInDto = new ObjectMapper().readValue(request.getInputStream(), RequestMemberSignInDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // 2. signInDto로 UsernamePasswordAuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(signInDto.getEmail(), signInDto.getPassword());

        // authenticationManager로 인증시도를 하면 PrincipalDetailsService - loadUserByUsername()가 호출
        // 만약 AuthenticationProvider를 구현한 CustomAuthenticationProvider가 @Component롤 등록되어있다면 : 구현하지 않는다면 스프링 자체적으로 실행됨
        // CustomAuthenticationProvider의 authenticate() 메서드가 실행됨
        // AuthenticationManager -> AuthenticationProvider, authenticate() 메서드 실행
        AuthenticationManager authenticationManager = getAuthenticationManager();
        return authenticationManager.authenticate(authenticationToken);
    }
}
