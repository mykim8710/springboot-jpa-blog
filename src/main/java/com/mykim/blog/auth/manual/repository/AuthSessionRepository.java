package com.mykim.blog.auth.manual.repository;

import com.mykim.blog.auth.manual.domain.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {
    Optional<AuthSession> findByAccessToken(String accessToken);

    @Query("select a from AuthSession a left join a.member m where m.id =:memberId and a.isActive = true")
    Optional<AuthSession> findByMemberId(@Param("memberId") Long memberId);
}
