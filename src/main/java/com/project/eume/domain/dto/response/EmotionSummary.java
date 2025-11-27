package com.project.eume.domain.dto.response;

import java.util.Map;

public record EmotionSummary(
    long needAttentionUsers,
    double avgEmotionScore,
    Map<String, Long> emotionDistribution
) {}
