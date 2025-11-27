package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.Sigungu;

public record AdminSigunguResponse(
        Long id,
        String sido,
        String sigungu
) {
    public static AdminSigunguResponse from(Sigungu sigungu) {
        return new AdminSigunguResponse(
                sigungu.getId(),
                sigungu.getSido(),
                sigungu.getSigungu()
        );
    }
}
