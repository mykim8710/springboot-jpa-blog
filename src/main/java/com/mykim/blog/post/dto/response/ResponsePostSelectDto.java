package com.mykim.blog.post.dto.response;

import com.mykim.blog.post.domain.Post;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class ResponsePostSelectDto {
    private Long id;
    private String title;
    private String content;

    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    private String username;


    // 생성자 오버로딩
    public ResponsePostSelectDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdDate = post.getCreatedDate();
        this.lastModifiedDate = post.getLastModifiedDate();
        this.username = post.getMember() == null ? "" : post.getMember().getUsername();
    }

    @Builder
    @QueryProjection
    public ResponsePostSelectDto(Long id, String title, String content,
                                 LocalDateTime createdDate, LocalDateTime lastModifiedDate,
                                 String username) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.username = username == null ? "" : username;
    }
}
