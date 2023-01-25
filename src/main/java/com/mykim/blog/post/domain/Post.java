package com.mykim.blog.post.domain;

import com.mykim.blog.global.entity.BaseTimeEntity;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.post.dto.request.RequestPostCreateDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long id;

    private String title;

    @Lob
    private String content;

    //(fetch = FetchType.LAZY)
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Builder
    private Post(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

    public static Post createPost(RequestPostCreateDto postCreateDto) {
        return Post.builder()
                        .title(postCreateDto.getTitle())
                        .content(postCreateDto.getContent())
                        .build();
    }

    public void editPost(PostEditor postEditor) {
        this.title = postEditor.getTitle();
        this.content = postEditor.getContent();
    }


    public static Post createPost(RequestPostCreateDto postCreateDto, Member member) {
        return Post.builder()
                        .title(postCreateDto.getTitle())
                        .content(postCreateDto.getContent())
                        .member(member)
                        .build();
    }




}

