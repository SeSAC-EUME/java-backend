package com.project.eume.domain.repository;

import com.project.eume.domain.entity.UserEmotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserEmotionRepository extends JpaRepository<UserEmotion, Long> {
    Page<UserEmotion> findByEumeUserIdOrderByAnalysisDateDesc(Long eumeUserId, Pageable pageable);

    Page<UserEmotion> findByEumeUserIdAndAnalysisDateBetweenOrderByAnalysisDateDesc(
            Long eumeUserId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

    /**
     * 여러 사용자의 최근 감정 데이터를 한 번에 조회
     * 각 사용자별로 가장 최근 분석 데이터 1건씩 반환
     */
    @Query("""
            SELECT e FROM UserEmotion e
            WHERE e.eumeUser.id IN :userIds
            AND e.analysisDate BETWEEN :startDate AND :endDate
            AND e.analysisDate = (
                SELECT MAX(e2.analysisDate) FROM UserEmotion e2
                WHERE e2.eumeUser.id = e.eumeUser.id
                AND e2.analysisDate BETWEEN :startDate AND :endDate
            )
            """)
    List<UserEmotion> findLatestEmotionsByUserIds(
            @Param("userIds") List<Long> userIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 여러 사용자의 두 번째 최근 감정 데이터를 조회 (추세 계산용)
     */
    @Query("""
            SELECT e FROM UserEmotion e
            WHERE e.eumeUser.id IN :userIds
            AND e.analysisDate BETWEEN :startDate AND :endDate
            AND e.analysisDate = (
                SELECT MAX(e2.analysisDate) FROM UserEmotion e2
                WHERE e2.eumeUser.id = e.eumeUser.id
                AND e2.analysisDate BETWEEN :startDate AND :endDate
                AND e2.analysisDate < (
                    SELECT MAX(e3.analysisDate) FROM UserEmotion e3
                    WHERE e3.eumeUser.id = e.eumeUser.id
                    AND e3.analysisDate BETWEEN :startDate AND :endDate
                )
            )
            """)
    List<UserEmotion> findPreviousEmotionsByUserIds(
            @Param("userIds") List<Long> userIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 감정 분포 통계를 SQL에서 직접 집계
     * 반환: List containing single Object[] = [totalWithData, safe, caution, highRisk, critical]
     */
    @Query("""
            SELECT
                COUNT(e),
                SUM(CASE WHEN e.emotionScore >= 0 AND e.emotionScore <= 29 THEN 1 ELSE 0 END),
                SUM(CASE WHEN e.emotionScore >= 30 AND e.emotionScore <= 59 THEN 1 ELSE 0 END),
                SUM(CASE WHEN e.emotionScore >= 60 AND e.emotionScore <= 79 THEN 1 ELSE 0 END),
                SUM(CASE WHEN e.emotionScore >= 80 THEN 1 ELSE 0 END)
            FROM UserEmotion e
            WHERE e.analysisDate BETWEEN :startDate AND :endDate
            AND e.analysisDate = (
                SELECT MAX(e2.analysisDate) FROM UserEmotion e2
                WHERE e2.eumeUser.id = e.eumeUser.id
                AND e2.analysisDate BETWEEN :startDate AND :endDate
            )
            """)
    List<Object[]> getEmotionStatistics(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
