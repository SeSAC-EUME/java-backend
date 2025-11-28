package com.project.eume.domain.repository;

import com.project.eume.domain.entity.EumeUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EumeUserRepository extends JpaRepository<EumeUser, Long> {
    Optional<EumeUser> findByEmail(String email);

    List<EumeUser> findByUserStatus(String userStatus);

    // 보고서용 통계 메서드
    long countByUserStatus(String userStatus);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
