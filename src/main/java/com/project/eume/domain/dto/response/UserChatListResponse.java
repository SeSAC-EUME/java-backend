package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.UserChatList;

import java.time.LocalDateTime;

public record UserChatListResponse(
        Long id,
        String roomTitle,
        String roomStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserChatListResponse from(UserChatList chatList) {
        return new UserChatListResponse(
                chatList.getId(),
                chatList.getRoomTitle(),
                chatList.getRoomStatus(),
                chatList.getCreatedAt(),
                chatList.getUpdatedAt()
        );
    }
}
