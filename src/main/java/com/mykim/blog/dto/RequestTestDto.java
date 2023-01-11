package com.mykim.blog.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestTestDto {
    private String title;
    private String content;

    public RequestTestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}

