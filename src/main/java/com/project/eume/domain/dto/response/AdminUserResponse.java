package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.EumeUser;

import java.time.LocalDateTime;

public record AdminUserResponse(
        Long id,
        String email,
        String userName,
        String nickname,
        String userStatus,
        String sigunguName,
        LocalDateTime lastLoginDate,
        LocalDateTime createdAt
) {
    public static AdminUserResponse from(EumeUser user) {
        String sigunguName = user.getSigungu() != null
                ? user.getSigungu().getSigungu()
                : null;

        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.getUserName(),
                user.getNickname(),
                user.getUserStatus(),
                sigunguName,
                user.getLastLoginDate(),
                user.getCreatedAt()
        );
    }
}
