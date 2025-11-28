package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.EumeChatContent;
import org.springframework.data.domain.Page;

import java.util.List;

public record EumeChatContentListResponse(
        List<EumeChatContentResponse> contents,
        int currentPage,
        int totalPages,
        long totalElements,
        boolean hasNext,
        boolean hasPrevious
) {
    public static EumeChatContentListResponse from(Page<EumeChatContent> page) {
        List<EumeChatContentResponse> contents = page.getContent().stream()
                .map(EumeChatContentResponse::from)
                .toList();

        return new EumeChatContentListResponse(
                contents,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}
