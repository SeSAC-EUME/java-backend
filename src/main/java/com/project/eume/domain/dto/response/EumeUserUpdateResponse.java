package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.entity.Sigungu;
import com.project.eume.domain.enums.BackgroundTheme;

import java.time.LocalDate;

public record EumeUserUpdateResponse (
    String userName,
    String nickName,
    String profileImage,
    Sigungu sigungu,
    LocalDate birthDate,
    String gender,
    String phone,
    BackgroundTheme backgroundTheme
) {
    public static EumeUserUpdateResponse from(EumeUser user) {
        return new EumeUserUpdateResponse(
            user.getUserName(),
            user.getNickname(),
            user.getProfileImage(),
            user.getSigungu(),
            user.getBirthDate(),
            user.getGender(),
            user.getPhone(),
            user.getBackgroundTheme()
        );
    }
}
