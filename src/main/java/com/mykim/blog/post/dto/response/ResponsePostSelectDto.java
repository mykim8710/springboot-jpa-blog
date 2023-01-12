package com.mykim.blog.post.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ResponsePostSelectDto {
    private String title;
    private String content;

    @Builder
    public ResponsePostSelectDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
