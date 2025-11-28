package com.project.eume.domain.repository;

import com.project.eume.domain.entity.EumeChatList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EumeChatListRepository extends JpaRepository<EumeChatList, Long> {
    Optional<EumeChatList> findByEumeUserId(Long eumeUserId);
    boolean existsByEumeUserId(Long eumeUserId);

    // 보고서용 통계 메서드
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
