package com.mykim.blog.member.domain;

import com.mykim.blog.global.entity.BaseTimeEntity;
import com.mykim.blog.member.dto.request.RequestMemberInsertDto;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "authorizationSession")
public class Member extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;
    private String email;
    private String password;
    private String username;

//    @OneToOne(mappedBy = "member")
//    private AuthorizationSession authorizationSession;

    @Builder
    public Member(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public static Member createMember(RequestMemberInsertDto memberInsertDto) {
        return Member.builder()
                        .email(memberInsertDto.getEmail())
                        .password(memberInsertDto.getPassword())
                        .username(memberInsertDto.getUsername())
                        .build();
    }
}
