package com.mykim.blog.post.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class RequestPostUpdateDto {
    @NotBlank(message = "글 제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "글 내용을 입력해주세요.")
    private String content;

    @Builder
    public RequestPostUpdateDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
