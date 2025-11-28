package com.project.eume.domain.dto.response;

public record ConversationSummary(
    long totalConversations,
    double dailyAvgConversations,
    long totalMessages
) {}
