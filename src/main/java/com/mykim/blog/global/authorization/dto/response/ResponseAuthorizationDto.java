package com.mykim.blog.global.authorization.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ResponseAuthorizationDto {
    private Long memberId;
    private String email;
    private String username;

    @Builder
    public ResponseAuthorizationDto(Long memberId, String email, String username) {
        this.memberId = memberId;
        this.email = email;
        this.username = username;
    }
}
