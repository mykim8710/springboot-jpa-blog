package com.mykim.blog.member.domain;

import com.mykim.blog.global.entity.BaseEntity;
import com.mykim.blog.global.entity.BaseTimeEntity;
import com.mykim.blog.member.dto.request.RequestMemberInsertDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    private String username;
    private String password;

    @Builder
    public Member(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static Member createMember(RequestMemberInsertDto memberInsertDto) {
        return Member.builder()
                            .username(memberInsertDto.getUsername())
                            .password(memberInsertDto.getPassword())
                            .build();
    }

}
