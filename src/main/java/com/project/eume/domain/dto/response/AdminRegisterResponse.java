package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.EumeAdmin;

import java.time.LocalDateTime;

public record AdminRegisterResponse(
        Long id,
        String adminLoginId,
        String adminName,
        String adminEmail,
        AdminSigunguResponse sigungu,
        LocalDateTime createdAt
) {
    public static AdminRegisterResponse from(EumeAdmin admin) {
        AdminSigunguResponse sigungu = admin.getSigungu() != null
                ? AdminSigunguResponse.from(admin.getSigungu())
                : null;

        return new AdminRegisterResponse(
                admin.getId(),
                admin.getAdminLoginId(),
                admin.getAdminName(),
                admin.getAdminEmail(),
                sigungu,
                admin.getCreatedAt()
        );
    }
}
