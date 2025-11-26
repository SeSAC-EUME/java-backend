package com.project.eume.domain.dto.request;

import com.project.eume.domain.enums.BackgroundTheme;

import java.time.LocalDate;

public record EumeUserUpdateRequest(
    String userName,
    String nickName,
    String profileImage,
    Long sigunguId,
    LocalDate birthDate,
    String gender,
    String phone,
    BackgroundTheme backgroundTheme
) {
}
