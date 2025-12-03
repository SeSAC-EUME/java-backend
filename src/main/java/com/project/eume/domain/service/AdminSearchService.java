package com.project.eume.domain.service;

import com.project.eume.domain.dto.response.AdminEmotionStatisticsResponse;
import com.project.eume.domain.dto.response.AdminUserEmotionLatestResponse;
import com.project.eume.domain.dto.response.AdminUserEmotionLatestResponse.UserWithLatestEmotion;
import com.project.eume.domain.entity.EumeAdmin;
import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.entity.UserEmotion;
import com.project.eume.domain.repository.EumeAdminRepository;
import com.project.eume.domain.repository.EumeChatListRepository;
import com.project.eume.domain.repository.EumeUserRepository;
import com.project.eume.domain.repository.UserEmotionRepository;
import com.project.eume.exceptions.errorcode.AdminErrorCode;
import com.project.eume.exceptions.exception.AdminException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AdminSearchService {
    private final EumeAdminRepository eumeAdminRepository;
    private final EumeUserRepository eumeUserRepository;
    private final UserEmotionRepository userEmotionRepository;
    private final EumeChatListRepository eumeChatListRepository;

    public EumeAdmin findByAdminLoginId(String adminLoginId) {
        return eumeAdminRepository.findByAdminLoginId(adminLoginId)
                .orElseThrow(() -> new AdminException(AdminErrorCode.ADMIN_NOT_FOUND));
    }

    public Page<EumeUser> findUsers(int page, int size, String status, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        // 기본 조회 (필터링은 추후 QueryDSL 등으로 확장 가능)
        return eumeUserRepository.findAll(pageable);
    }

    public EumeUser findUserById(Long userId) {
        return eumeUserRepository.findById(userId)
                .orElseThrow(() -> new AdminException(AdminErrorCode.USER_NOT_FOUND));
    }

    public Page<UserEmotion> findUserEmotions(Long userId, int page, int size, LocalDate startDate, LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size);

        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            return userEmotionRepository.findByEumeUserIdAndAnalysisDateBetweenOrderByAnalysisDateDesc(
                    userId, start, end, pageable
            );
        }

        return userEmotionRepository.findByEumeUserIdOrderByAnalysisDateDesc(userId, pageable);
    }


    /**
     * 전체 사용자 조회 (Excel 내보내기용, 페이지네이션 없음)
     *
     * @param status  상태 필터 (nullable)
     * @param keyword 검색어 (nullable)
     * @return 사용자 목록
     */
    public List<EumeUser> findAllUsers(String status, String keyword) {
        // 기본 조회 (필터링은 추후 QueryDSL 등으로 확장 가능)
        if (status != null && !status.isEmpty()) {
            return eumeUserRepository.findByUserStatus(status);
        }
        return eumeUserRepository.findAll();
    }

    /**
     * 모든 사용자의 최근 감정 분석 데이터를 한 번에 조회
     * N+1 문제를 해결하기 위해 배치 조회 사용
     *
     * @param page         페이지 번호
     * @param size         페이지당 항목 수
     * @param startDate    조회 시작일
     * @param endDate      조회 종료일
     * @param emotionLevel 감정 레벨 필터 (all, safe, caution, danger)
     * @return 사용자별 최근 감정 데이터
     */
    public AdminUserEmotionLatestResponse findUsersWithLatestEmotion(
            int page, int size, LocalDate startDate, LocalDate endDate, String emotionLevel
    ) {
        // 1. 날짜 기본값 설정
        LocalDate effectiveStartDate = startDate != null ? startDate : LocalDate.now().minusDays(7);
        LocalDate effectiveEndDate = endDate != null ? endDate : LocalDate.now();

        LocalDateTime start = effectiveStartDate.atStartOfDay();
        LocalDateTime end = effectiveEndDate.atTime(LocalTime.MAX);

        // 2. 사용자 페이징 조회
        Pageable pageable = PageRequest.of(page, size);
        Page<EumeUser> userPage = eumeUserRepository.findAll(pageable);

        if (userPage.isEmpty()) {
            return AdminUserEmotionLatestResponse.from(userPage, List.of());
        }

        // 3. 사용자 ID 목록 추출
        List<Long> userIds = userPage.getContent().stream()
                .map(EumeUser::getId)
                .toList();

        // 4. 최근 감정 데이터 일괄 조회
        List<UserEmotion> latestEmotions = userEmotionRepository.findLatestEmotionsByUserIds(userIds, start, end);
        Map<Long, UserEmotion> latestEmotionMap = latestEmotions.stream()
                .collect(Collectors.toMap(e -> e.getEumeUser().getId(), Function.identity()));

        // 5. 이전 감정 데이터 일괄 조회 (추세 계산용)
        List<UserEmotion> previousEmotions = userEmotionRepository.findPreviousEmotionsByUserIds(userIds, start, end);
        Map<Long, UserEmotion> previousEmotionMap = previousEmotions.stream()
                .collect(Collectors.toMap(e -> e.getEumeUser().getId(), Function.identity()));

        // 6. 대화 수 일괄 조회
        List<Object[]> conversationCounts = eumeChatListRepository.countByUserIds(userIds);
        Map<Long, Long> conversationCountMap = conversationCounts.stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));

        // 7. 결과 조합
        List<UserWithLatestEmotion> usersWithEmotion = new ArrayList<>();
        for (EumeUser user : userPage.getContent()) {
            UserEmotion latestEmotion = latestEmotionMap.get(user.getId());
            UserEmotion previousEmotion = previousEmotionMap.get(user.getId());
            Long conversationCount = conversationCountMap.getOrDefault(user.getId(), 0L);

            Integer previousScore = previousEmotion != null ? previousEmotion.getEmotionScore() : null;

            // 감정 레벨 필터링
            if (shouldIncludeByEmotionLevel(latestEmotion, emotionLevel)) {
                usersWithEmotion.add(UserWithLatestEmotion.from(
                        user, latestEmotion, previousScore, conversationCount
                ));
            }
        }

        return AdminUserEmotionLatestResponse.from(userPage, usersWithEmotion);
    }

    /**
     * 감정 레벨 필터링 조건 확인
     */
    private boolean shouldIncludeByEmotionLevel(UserEmotion emotion, String emotionLevel) {
        if (emotionLevel == null || "all".equalsIgnoreCase(emotionLevel)) {
            return true;
        }

        if (emotion == null) {
            return false; // 감정 데이터가 없으면 필터링된 결과에서 제외
        }

        int score = emotion.getEmotionScore();
        return switch (emotionLevel.toLowerCase()) {
            case "safe" -> score >= 0 && score <= 29;
            case "caution" -> score >= 30 && score <= 59;
            case "danger" -> score >= 60;
            default -> true;
        };
    }

    /**
     * 감정 분포 통계 조회
     * 감정 분포 차트를 위한 집계 데이터 반환
     * SQL에서 직접 집계하여 효율적으로 처리
     *
     * @param startDate 조회 시작일
     * @param endDate   조회 종료일
     * @return 감정 분포 통계
     */
    public AdminEmotionStatisticsResponse getEmotionStatistics(LocalDate startDate, LocalDate endDate) {
        // 1. 날짜 기본값 설정
        LocalDate effectiveStartDate = startDate != null ? startDate : LocalDate.now().minusDays(7);
        LocalDate effectiveEndDate = endDate != null ? endDate : LocalDate.now();

        LocalDateTime start = effectiveStartDate.atStartOfDay();
        LocalDateTime end = effectiveEndDate.atTime(LocalTime.MAX);

        // 2. 전체 사용자 수 조회 (단순 count 쿼리)
        long totalUsers = eumeUserRepository.count();

        if (totalUsers == 0) {
            return AdminEmotionStatisticsResponse.of(
                    effectiveStartDate, effectiveEndDate, 0, 0, 0, 0, 0, 0
            );
        }

        // 3. SQL에서 직접 감정 통계 집계 (단일 쿼리)
        Object[] stats = userEmotionRepository.getEmotionStatistics(start, end);

        long usersWithData = stats[0] != null ? ((Number) stats[0]).longValue() : 0;
        long safe = stats[1] != null ? ((Number) stats[1]).longValue() : 0;
        long caution = stats[2] != null ? ((Number) stats[2]).longValue() : 0;
        long highRisk = stats[3] != null ? ((Number) stats[3]).longValue() : 0;
        long critical = stats[4] != null ? ((Number) stats[4]).longValue() : 0;
        long noData = totalUsers - usersWithData;

        return AdminEmotionStatisticsResponse.of(
                effectiveStartDate, effectiveEndDate, totalUsers,
                safe, caution, highRisk, critical, noData
        );
    }
}
