package com.project.eume.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtRule {
    JWT_ISSUE_HEADER("Set-Cookie"),
    JWT_RESOLVE_HEADER("Cookie"),
    REFRESH_PREFIX("eume_refresh_token"), // TODO 1: Refresh Token 추가 예정
    ACCESS_PREFIX("eume_access_token"),
    ADMIN_ACCESS_PREFIX("eume_admin_access_token");

    private final String value;

    /**
     * jwtType에 대응되는 prefix 값을 반환하는 메서드
     *
     * @param jwtType jwt 토큰 유형
     * @return jwtType에 대응되는 prefix 값
     */
    public static JwtRule getPrefix(JwtType jwtType) {
        return jwtType.isAccessToken() ? ACCESS_PREFIX : REFRESH_PREFIX;
    }
}
