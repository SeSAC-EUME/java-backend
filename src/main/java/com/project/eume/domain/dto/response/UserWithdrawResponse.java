package com.project.eume.domain.dto.response;

import java.time.LocalDateTime;

public record UserWithdrawResponse(
    String message,
    LocalDateTime withdrawnAt
) {
    public static UserWithdrawResponse success() {
        return new UserWithdrawResponse(
            "계정이 탈퇴 처리되었습니다",
            LocalDateTime.now()
        );
    }
}
