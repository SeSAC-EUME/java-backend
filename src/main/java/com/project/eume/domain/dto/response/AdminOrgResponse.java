package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.Sigungu;

public record AdminOrgResponse(
        Long id,
        String name
) {
    public static AdminOrgResponse from(Sigungu sigungu) {
        String name = sigungu.getSido() + " " + sigungu.getSigungu();
        return new AdminOrgResponse(sigungu.getId(), name);
    }
}
