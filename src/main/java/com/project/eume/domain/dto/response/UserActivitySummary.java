package com.project.eume.domain.dto.response;

public record UserActivitySummary(
    long totalUsers,
    long activeUsers,
    long newUsers
) {}
