package com.project.eume.domain.dto.response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.entity.UserEmotion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 모든 사용자의 최근 감정 분석 데이터를 조회하는 API 응답 DTO
 * N+1 문제 해결을 위해 한 번의 API 호출로 모든 데이터를 반환
 */
@Slf4j
public record AdminUserEmotionLatestResponse(
        long totalElements,
        int totalPages,
        int currentPage,
        int size,
        List<UserWithLatestEmotion> users
) {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static AdminUserEmotionLatestResponse from(
            Page<EumeUser> userPage,
            List<UserWithLatestEmotion> usersWithEmotion
    ) {
        return new AdminUserEmotionLatestResponse(
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.getNumber(),
                userPage.getSize(),
                usersWithEmotion
        );
    }

    /**
     * 사용자 정보와 최근 감정 데이터를 포함하는 내부 DTO
     */
    public record UserWithLatestEmotion(
            Long userId,
            String userName,
            String nickname,
            LocalDate birthDate,
            String profileImage,
            LocalDateTime lastLoginDate,
            Long conversationCount,
            LatestEmotion latestEmotion,
            Integer previousEmotionScore,
            boolean hasEmotionData
    ) {
        public static UserWithLatestEmotion from(
                EumeUser user,
                UserEmotion latestEmotion,
                Integer previousEmotionScore,
                Long conversationCount
        ) {
            return new UserWithLatestEmotion(
                    user.getId(),
                    user.getUserName(),
                    user.getNickname(),
                    user.getBirthDate(),
                    user.getProfileImage(),
                    user.getLastLoginDate(),
                    conversationCount,
                    latestEmotion != null ? LatestEmotion.from(latestEmotion) : null,
                    previousEmotionScore,
                    latestEmotion != null
            );
        }
    }

    /**
     * 최근 감정 분석 데이터 DTO
     */
    public record LatestEmotion(
            Integer emotionScore,
            Integer depressionScore,
            Integer anxietyScore,
            Integer stressScore,
            List<String> keywords,
            LocalDateTime analysisDate
    ) {
        public static LatestEmotion from(UserEmotion emotion) {
            List<String> keywordList = parseKeywords(emotion.getKeywords());

            return new LatestEmotion(
                    emotion.getEmotionScore(),
                    emotion.getDepressionScore(),
                    emotion.getAnxietyScore(),
                    emotion.getStressScore(),
                    keywordList,
                    emotion.getAnalysisDate()
            );
        }

        private static List<String> parseKeywords(String keywords) {
            if (keywords == null || keywords.isBlank()) {
                return List.of();
            }
            try {
                return objectMapper.readValue(keywords, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                log.warn("키워드 파싱 실패: {}", keywords, e);
                return List.of();
            }
        }
    }
}
