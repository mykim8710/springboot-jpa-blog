package com.mykim.blog.auth.manual.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ResponseAuthDto {
    private Long memberId;
    private String email;
    private String username;

    @Builder
    public ResponseAuthDto(Long memberId, String email, String username) {
        this.memberId = memberId;
        this.email = email;
        this.username = username;
    }
}
