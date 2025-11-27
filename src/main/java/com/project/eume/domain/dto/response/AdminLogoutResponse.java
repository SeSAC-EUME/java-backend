package com.project.eume.domain.dto.response;

public record AdminLogoutResponse(
        String message,
        boolean isSuccess
) {
    public static AdminLogoutResponse success() {
        return new AdminLogoutResponse("로그아웃 되었습니다", true);
    }
}
