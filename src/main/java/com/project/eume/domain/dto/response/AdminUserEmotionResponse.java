package com.project.eume.domain.dto.response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.eume.domain.entity.UserEmotion;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
public record AdminUserEmotionResponse(
        Long id,
        Integer depressionScore,
        Integer anxietyScore,
        Integer stressScore,
        Integer emotionScore,
        List<String> keywords,
        LocalDateTime analysisDate,
        String modelVersion
) {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static AdminUserEmotionResponse from(UserEmotion emotion) {
        List<String> keywords = parseKeywords(emotion.getKeywords());

        return new AdminUserEmotionResponse(
                emotion.getId(),
                emotion.getDepressionScore(),
                emotion.getAnxietyScore(),
                emotion.getStressScore(),
                emotion.getEmotionScore(),
                keywords,
                emotion.getAnalysisDate(),
                emotion.getModelVersion()
        );
    }

    private static List<String> parseKeywords(String keywordsJson) {
        if (keywordsJson == null || keywordsJson.isBlank()) {
            return Collections.emptyList();
        }

        String trimmed = keywordsJson.trim();

        // JSON 배열이 아닌 경우 (숫자, 일반 문자열 등) 빈 리스트 반환
        if (!trimmed.startsWith("[")) {
            log.debug("Keywords is not a JSON array: {}", keywordsJson);
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(trimmed, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse keywords JSON: {}", keywordsJson, e);
            return Collections.emptyList();
        }
    }
}
