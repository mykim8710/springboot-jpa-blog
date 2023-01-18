package com.mykim.blog.global.config.web;

import com.mykim.blog.global.authorization.argumentresolver.JwtAuthArgumentResolver;
import com.mykim.blog.global.authorization.argumentresolver.SessionAuthArgumentResolver;
import com.mykim.blog.global.authorization.repository.AuthorizationSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final AuthorizationSessionRepository memberSessionRepository;

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
        //resolvers.add(new SessionAuthArgumentResolver(memberSessionRepository));
        resolvers.add(new JwtAuthArgumentResolver());


    }
}
