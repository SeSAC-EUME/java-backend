package com.project.eume.domain.dto.response;

import java.time.LocalDateTime;

public record AdminUserStatusUpdateResponse(
    Long userId,
    String previousStatus,
    String currentStatus,
    LocalDateTime updatedAt
) {
    public static AdminUserStatusUpdateResponse of(Long userId, String prevStatus, String curStatus) {
        return new AdminUserStatusUpdateResponse(
            userId,
            prevStatus,
            curStatus,
            LocalDateTime.now()
        );
    }
}
