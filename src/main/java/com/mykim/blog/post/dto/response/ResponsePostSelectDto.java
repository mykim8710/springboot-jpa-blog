package com.mykim.blog.post.dto.response;

import com.mykim.blog.post.domain.Post;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ResponsePostSelectDto {
    private Long id;
    private String title;
    private String content;

    // 생성자 오버로딩
    public ResponsePostSelectDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
    }

    @Builder
    @QueryProjection
    public ResponsePostSelectDto(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}
