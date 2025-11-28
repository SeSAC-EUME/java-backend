package com.project.eume.domain.repository;

import com.project.eume.domain.entity.EumeChatContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface EumeChatContentRepository extends JpaRepository<EumeChatContent, Long> {
    Page<EumeChatContent> findByEumeChatListIdOrderByCreatedAtDesc(Long eumeChatListId, Pageable pageable);

    // 보고서용 통계 메서드
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
