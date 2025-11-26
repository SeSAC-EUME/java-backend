package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.enums.BackgroundTheme;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EumeUserProfileResponse (
    String email,
    String userName,
    String nickname,
    String profileImage,
    EumeUserProfileSigunguReponse sigungu,
    LocalDate birthDate,
    String gender,
    String phone,
    LocalDateTime lastLoginDate,
    BackgroundTheme backGroundTheme,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    /**
     * EumeUser 엔티티로부터 EumeUserProfileResponse 생성
     *
     * @param user EumeUser 엔티티
     * @return EumeUserProfileResponse
     */
    public static EumeUserProfileResponse from(EumeUser user) {
        return new EumeUserProfileResponse(
            user.getEmail(),
            user.getUserName(),
            user.getNickname(),
            user.getProfileImage(),
            EumeUserProfileSigunguReponse.from(user.getSigungu()),
            user.getBirthDate(),
            user.getGender(),
            user.getPhone(),
            user.getLastLoginDate(),
            user.getBackgroundTheme(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
