package com.project.eume.domain.dto.response;

import java.util.List;

public record AdminReportSummaryResponse(
    UserActivitySummary userActivity,
    ConversationSummary conversation,
    EmotionSummary emotion,
    List<DailyStatResponse> dailyStats
) {
    public static AdminReportSummaryResponse of(
        UserActivitySummary userActivity,
        ConversationSummary conversation,
        EmotionSummary emotion,
        List<DailyStatResponse> dailyStats
    ) {
        return new AdminReportSummaryResponse(userActivity, conversation, emotion, dailyStats);
    }
}
