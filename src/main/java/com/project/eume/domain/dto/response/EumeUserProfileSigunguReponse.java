package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.Sigungu;

public record EumeUserProfileSigunguReponse(
    String sido,
    String sigungu
) {
    public static EumeUserProfileSigunguReponse from(Sigungu sigungu) {
        if (sigungu == null) {
            return new EumeUserProfileSigunguReponse(
                "",
                ""
            );
        }

        return new EumeUserProfileSigunguReponse(
            sigungu.getSido(),
            sigungu.getSigungu()
        );
    }
}
