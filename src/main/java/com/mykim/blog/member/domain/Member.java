package com.mykim.blog.member.domain;

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
public class Member extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;
    private String email;
    private String password;
    private String username;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @Builder
    public Member(String username, String email, String password, MemberRole memberRole) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.memberRole = memberRole;
    }

    public static Member createMember(RequestMemberInsertDto memberInsertDto) {
        return Member.builder()
                        .email(memberInsertDto.getEmail())
                        .password(memberInsertDto.getPassword())
                        .username(memberInsertDto.getUsername())
                        .memberRole(memberInsertDto.getMemberRole())
                        .build();
    }
}
