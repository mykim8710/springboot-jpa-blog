package com.mykim.blog.auth.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import spring.security.jwt.config.security.principal.PrincipalDetail;
import spring.security.jwt.domain.User;
import spring.security.jwt.repository.UserRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private static final List<String> EXCLUDED_URL = List.of("/test");// jwt 토큰 검증이 필요없는 url 추가

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, JwtProvider jwtProvider) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.jwtProvider  = jwtProvider;
    }

    /**
     * 인증이나 권한이 필요한 url요청 시 이 필터를 타게됨
     * => jwt 토큰 검증이 필요한 api, url 요청 시 작동
     * => jwt 토큰 검증이 필요없는 api, url 설정가능
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("JwtAuthorizationFilter 동작");

        // jwt 토큰을 검증해서 정상적이 사용자인지 확인
        String jwt = jwtProvider.resolveJwt(request);
        log.info("jwt Token = {}", jwt);

        if(StringUtils.hasText(jwt) && jwtProvider.isValidTokenExpireDate(jwt)) {
            Integer userId = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET_KEY))
                                    .build()
                                    .verify(jwt)
                                    .getClaim("id").asInt();
            log.info("userId = {}", userId);
            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new UsernameNotFoundException("없는 사용자입니다."));
            PrincipalDetail principalDetail = new PrincipalDetail(user);

            // jwt 토큰 서명을 통해서 서명이 정상이면 Authentication객체를 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetail, null, principalDetail.getAuthorities());

            // 강제로 시큐리티의 세션에 접근하여 값 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    // jwt 토큰 검증이 필요없는 url 추가
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        return EXCLUDED_URL.stream().anyMatch(exclude -> antPathMatcher.match(exclude, request.getServletPath()));
    }
}
