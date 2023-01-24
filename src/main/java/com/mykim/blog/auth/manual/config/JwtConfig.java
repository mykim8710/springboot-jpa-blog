package com.mykim.blog.auth.manual.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Getter
@Component
public class JwtConfig {
    @Value("${custom-jwt.key}")
    private String key;

    public byte[] getDecodeKey() {
        return Base64.getDecoder().decode(this.key);
    }
}
