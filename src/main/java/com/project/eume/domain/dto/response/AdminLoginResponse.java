package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.EumeAdmin;

import java.time.LocalDateTime;

public record AdminLoginResponse(
        Long id,
        String adminName,
        String adminEmail,
        LocalDateTime lastLoginDate
) {
    public static AdminLoginResponse from(EumeAdmin admin) {
        return new AdminLoginResponse(
                admin.getId(),
                admin.getAdminName(),
                admin.getAdminEmail(),
                admin.getLastLoginDate()
        );
    }
}
