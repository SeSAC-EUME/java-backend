package com.project.eume.domain.dto.response;

import java.time.LocalDate;

public record DailyStatResponse(
    LocalDate date,
    long activeUsers,
    long conversations,
    double avgEmotionScore
) {}
