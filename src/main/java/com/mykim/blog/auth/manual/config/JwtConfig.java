package com.mykim.blog.auth.manual.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Getter
@Component
public class JwtConfig {
    private String key = "jiS48dIGRtD73A/st4gd8SxL7AHSdkPtNb7oO9p22rI=";

    public byte[] getDecodeKey() {
        return Base64.getDecoder().decode(this.key);
    }
}
