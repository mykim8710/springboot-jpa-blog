package com.mykim.blog.auth.manual.domain;

import com.mykim.blog.member.domain.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "member")
public class AuthSession {
    private static final int ACCESS_TOKEN_VALIDATION_DAY = 1; // 30 days

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="AUTH_SESSION_ID")
    private Long id;

    private String accessToken;  // == sessionId

    private LocalDateTime tokenIssueTime;
    private LocalDateTime tokenExpirationTime;

    private boolean isActive;

    // AuthSession <-> Member, 1 : 1
    // 연관관계의 주인 : MEMBER_ID(fk)를 가짐
    @ManyToOne(fetch = FetchType.EAGER) // cascade = CascadeType.ALL
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Builder
    public AuthSession(Member member) {
        this.accessToken = UUID.randomUUID().toString();
        this.tokenIssueTime = LocalDateTime.now();
        this.tokenExpirationTime = LocalDateTime.now().plusDays(ACCESS_TOKEN_VALIDATION_DAY);
        this.member = member;
        this.isActive = true;
    }

    public void deactivateAuthSession() {
        this.isActive = false;
    }

}
