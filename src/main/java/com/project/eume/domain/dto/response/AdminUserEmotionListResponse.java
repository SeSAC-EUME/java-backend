package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.UserEmotion;
import org.springframework.data.domain.Page;

import java.util.List;

public record AdminUserEmotionListResponse(
        List<AdminUserEmotionResponse> emotions,
        int currentPage,
        int totalPages,
        long totalElements,
        boolean hasNext,
        boolean hasPrevious
) {
    public static AdminUserEmotionListResponse from(Page<UserEmotion> page) {
        List<AdminUserEmotionResponse> emotions = page.getContent().stream()
                .map(AdminUserEmotionResponse::from)
                .toList();

        return new AdminUserEmotionListResponse(
                emotions,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}
