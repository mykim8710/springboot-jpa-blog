package com.mykim.blog.global.config.authorization;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AuthorizationMember {
    private Long memberId;
    private String email;
    private String username;

    @Builder
    public AuthorizationMember(Long memberId, String email, String username) {
        this.memberId = memberId;
        this.email = email;
        this.username = username;
    }
}
