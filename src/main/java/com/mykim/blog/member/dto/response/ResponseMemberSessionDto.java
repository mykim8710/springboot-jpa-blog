package com.mykim.blog.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseMemberSessionDto {
    private String accessToken;

    @Builder
    public ResponseMemberSessionDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
