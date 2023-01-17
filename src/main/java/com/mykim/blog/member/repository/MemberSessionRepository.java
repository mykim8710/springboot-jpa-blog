package com.mykim.blog.member.repository;

import com.mykim.blog.member.domain.MemberSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberSessionRepository extends JpaRepository<MemberSession, Long> {
    Optional<MemberSession> findByAccessToken(String accessToken);
}
