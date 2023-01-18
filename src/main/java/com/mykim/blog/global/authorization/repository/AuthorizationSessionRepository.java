package com.mykim.blog.global.authorization.repository;

import com.mykim.blog.global.authorization.domain.AuthorizationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthorizationSessionRepository extends JpaRepository<AuthorizationSession, Long> {
    Optional<AuthorizationSession> findByAccessToken(String accessToken);

    @Query("select a from AuthorizationSession a left join a.member m where m.id =:memberId and a.isActive = true")
    Optional<AuthorizationSession> findByMemberId(@Param("memberId") Long memberId);
}
