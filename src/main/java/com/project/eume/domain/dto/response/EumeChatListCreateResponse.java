package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.EumeChatList;

import java.time.LocalDateTime;

public record EumeChatListCreateResponse(
        Long id,
        String chatStatus,
        LocalDateTime createdAt
) {
    public static EumeChatListCreateResponse from(EumeChatList chatList) {
        return new EumeChatListCreateResponse(
                chatList.getId(),
                chatList.getChatStatus().name(),
                chatList.getCreatedAt()
        );
    }
}
