package com.mykim.blog.auth.security.jwt;

import com.mykim.blog.auth.security.principal.PrincipalDetail;
import com.mykim.blog.global.result.error.ErrorCode;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.member.exception.UnAuthorizedMemberException;
import com.mykim.blog.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private static final List<String> EXCLUDED_URL = List.of("/sign-out", "/api/v1/auth/**");// jwt 토큰 검증이 필요없는 url 추가

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RedisTemplate redisTemplate;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
                                  MemberRepository memberRepository,
                                  JwtProvider jwtProvider,
                                  RedisTemplate redisTemplate) {

        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.jwtProvider  = jwtProvider;
        this.redisTemplate = redisTemplate;
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
            // redis로부터 이 토큰이 로그아웃된 토큰인지 확인
            String isSignOut = (String)redisTemplate.opsForValue().get(jwt);
            log.info("isSignOut : {}" , isSignOut);
            System.out.println("ObjectUtils.isEmpty(isSignOut) = " + ObjectUtils.isEmpty(isSignOut));

            if(ObjectUtils.isEmpty(isSignOut) == true) {
                Long userId = Long.valueOf(jwtProvider.getSubject(jwt));
                log.info("userId = {}", userId);

                Member member = memberRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new UsernameNotFoundException("없는 사용자입니다."));
                PrincipalDetail principalDetail = new PrincipalDetail(member);

                // jwt 토큰 서명을 통해서 서명이 정상이면 Authentication객체를 생성
                Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetail, null, principalDetail.getAuthorities());

                // 강제로 시큐리티의 세션에 접근하여 값 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
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
