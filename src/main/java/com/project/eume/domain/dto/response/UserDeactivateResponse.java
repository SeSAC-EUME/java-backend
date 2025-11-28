package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.EumeUser;
import java.time.LocalDateTime;

public record UserDeactivateResponse(
    String message,
    String userStatus,
    LocalDateTime deactivatedAt
) {
    public static UserDeactivateResponse from(EumeUser user) {
        return new UserDeactivateResponse(
            "계정이 비활성화되었습니다",
            user.getUserStatus(),
            LocalDateTime.now()
        );
    }
}
