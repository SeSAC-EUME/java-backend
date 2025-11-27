package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.UserChatList;

import java.time.LocalDateTime;

public record UserChatListCreateResponse(
        Long id,
        String roomTitle,
        String roomStatus,
        LocalDateTime createdAt
) {
    public static UserChatListCreateResponse from(UserChatList chatList) {
        return new UserChatListCreateResponse(
                chatList.getId(),
                chatList.getRoomTitle(),
                chatList.getRoomStatus(),
                chatList.getCreatedAt()
        );
    }
}
