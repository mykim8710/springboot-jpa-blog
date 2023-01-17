package com.mykim.blog.member.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "member")
public class MemberSession {
    private static final int ACCESS_TOKEN_VALIDATION_DAY = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="MEMBER_SESSION_ID")
    private Long id;

    private String accessToken;

    private LocalDateTime tokenExpirationTime;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Builder
    public MemberSession(Member member) {
        this.accessToken = UUID.randomUUID().toString();
        this.tokenExpirationTime = LocalDateTime.now().plusDays(ACCESS_TOKEN_VALIDATION_DAY);
        this.member = member;
    }
}
