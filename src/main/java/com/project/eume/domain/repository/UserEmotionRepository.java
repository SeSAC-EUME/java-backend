package com.project.eume.domain.repository;

import com.project.eume.domain.entity.UserEmotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface UserEmotionRepository extends JpaRepository<UserEmotion, Long> {
    Page<UserEmotion> findByEumeUserIdOrderByAnalysisDateDesc(Long eumeUserId, Pageable pageable);

    Page<UserEmotion> findByEumeUserIdAndAnalysisDateBetweenOrderByAnalysisDateDesc(
            Long eumeUserId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );
}
