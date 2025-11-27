package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.EumeChatList;

import java.time.LocalDateTime;

public record EumeChatListDetailResponse(
        Long id,
        String chatStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static EumeChatListDetailResponse from(EumeChatList chatList) {
        if (chatList == null) {
            return new EumeChatListDetailResponse(null, null, null, null);
        }
        return new EumeChatListDetailResponse(
                chatList.getId(),
                chatList.getChatStatus().name(),
                chatList.getCreatedAt(),
                chatList.getUpdatedAt()
        );
    }
}
