package com.mykim.blog.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);   // 내 서버가 응답 시, json을 자바스크립트에서 처리할 수 있게 할지 설정
        corsConfig.addAllowedOrigin("*");   // 모든 ip의 응답을 허용
        corsConfig.addAllowedHeader("*");   // 모든 header의 응답을 허용
        corsConfig.addAllowedMethod("*");   // 모든 http method(get, post, put, delete....)의 응답을 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", corsConfig); // /api/** 의 모든 요청은 이 config를 적용
        return new CorsFilter(source);
    }

}
