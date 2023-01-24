package com.mykim.blog.auth.security.jwt;

public class JwtProperties {
    public static String SECRET_KEY = "mykimJwtTokenSecretKey"; // 우리 서버만 알고 있는 비밀값
    public static int EXPIRATION_TIME = 864000000; // 10일 (1/1000초)
    public static String TOKEN_PREFIX = "Bearer ";
    public static String HEADER_STRING = "Authorization";
}
