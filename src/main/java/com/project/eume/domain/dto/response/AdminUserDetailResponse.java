package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.EumeUser;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AdminUserDetailResponse(
        Long id,
        String email,
        String userName,
        String nickname,
        String profileImage,
        String userStatus,
        String providerId,
        LocalDate birthDate,
        String gender,
        String phone,
        AdminSigunguResponse sigungu,
        String backgroundTheme,
        LocalDateTime lastLoginDate,
        Integer loginFailCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AdminUserDetailResponse from(EumeUser user) {
        AdminSigunguResponse sigungu = user.getSigungu() != null
                ? AdminSigunguResponse.from(user.getSigungu())
                : null;

        return new AdminUserDetailResponse(
                user.getId(),
                user.getEmail(),
                user.getUserName(),
                user.getNickname(),
                user.getProfileImage(),
                user.getUserStatus(),
                user.getProviderId(),
                user.getBirthDate(),
                user.getGender(),
                user.getPhone(),
                sigungu,
                user.getBackgroundTheme().name(),
                user.getLastLoginDate(),
                user.getLoginFailCount(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
