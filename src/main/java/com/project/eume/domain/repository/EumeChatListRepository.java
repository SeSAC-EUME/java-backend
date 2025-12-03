package com.project.eume.domain.repository;

import com.project.eume.domain.entity.EumeChatList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EumeChatListRepository extends JpaRepository<EumeChatList, Long> {
    Optional<EumeChatList> findByEumeUserId(Long eumeUserId);
    boolean existsByEumeUserId(Long eumeUserId);

    // 보고서용 통계 메서드
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 사용자별 대화 수 조회
    long countByEumeUserId(Long eumeUserId);

    /**
     * 여러 사용자의 대화 수를 한 번에 조회
     * Object[0]: userId (Long), Object[1]: count (Long)
     */
    @Query("""
            SELECT e.eumeUser.id, COUNT(e) FROM EumeChatList e
            WHERE e.eumeUser.id IN :userIds
            GROUP BY e.eumeUser.id
            """)
    List<Object[]> countByUserIds(@Param("userIds") List<Long> userIds);
}
