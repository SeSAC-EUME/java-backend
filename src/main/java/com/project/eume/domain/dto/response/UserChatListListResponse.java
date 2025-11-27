package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.UserChatList;
import org.springframework.data.domain.Page;

import java.util.List;

public record UserChatListListResponse(
        List<UserChatListResponse> chatRooms,
        int currentPage,
        int totalPages,
        long totalElements,
        boolean hasNext,
        boolean hasPrevious
) {
    public static UserChatListListResponse from(Page<UserChatList> page) {
        List<UserChatListResponse> chatRooms = page.getContent().stream()
                .map(UserChatListResponse::from)
                .toList();

        return new UserChatListListResponse(
                chatRooms,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}
