package com.mykim.blog.global.config.web;

import com.mykim.blog.auth.argumentresolver.JwtAuthArgumentResolver;
import com.mykim.blog.auth.argumentresolver.SessionAuthArgumentResolver;
import com.mykim.blog.auth.config.JwtConfig;
import com.mykim.blog.auth.repository.AuthSessionRepository;
import com.mykim.blog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final AuthSessionRepository memberSessionRepository;
    private final MemberRepository memberRepository;
    private final JwtConfig jwtConfig;

    // interceptor
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new AuthInterceptor())
//                .order(1)
//                .excludePathPatterns("/error", "/favicon.ico");
//    }

    // ArgumentResolver
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SessionAuthArgumentResolver(memberSessionRepository));
        resolvers.add(new JwtAuthArgumentResolver(memberRepository, jwtConfig));
    }
}
