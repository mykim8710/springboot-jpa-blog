package com.mykim.blog.post.domain;

import com.mykim.blog.post.dto.request.RequestPostCreateDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long id;

    private String title;

    @Lob
    private String content;

//    @Enumerated(EnumType.ORDINAL)
//    private PostCategory category;

    @Builder
    private Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static Post createPost(RequestPostCreateDto postCreateDto) {
        return Post.builder()
                        .title(postCreateDto.getTitle())
                        .content(postCreateDto.getContent())
                        .build();
    }

}

