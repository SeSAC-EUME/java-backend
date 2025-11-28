package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.UserChatContent;
import org.springframework.data.domain.Page;

import java.util.List;

public record UserChatContentListResponse(
        List<UserChatContentResponse> contents,
        int currentPage,
        int totalPages,
        long totalElements,
        boolean hasNext,
        boolean hasPrevious
) {
    public static UserChatContentListResponse from(Page<UserChatContent> page) {
        List<UserChatContentResponse> contents = page.getContent().stream()
                .map(UserChatContentResponse::from)
                .toList();

        return new UserChatContentListResponse(
                contents,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}
