package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.UserChatContent;

import java.time.LocalDateTime;

public record UserChatContentCreateResponse(
        Long id,
        Long senderId,
        String senderName,
        String messageType,
        String messageContent,
        LocalDateTime createdAt
) {
    public static UserChatContentCreateResponse from(UserChatContent content) {
        return new UserChatContentCreateResponse(
                content.getId(),
                content.getEumeUser().getId(),
                content.getEumeUser().getUserName(),
                content.getMessageType(),
                content.getMessageContent(),
                content.getCreatedAt()
        );
    }
}
