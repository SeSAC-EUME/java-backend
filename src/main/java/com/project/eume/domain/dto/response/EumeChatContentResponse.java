package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.EumeChatContent;

import java.time.LocalDateTime;

public record EumeChatContentResponse(
        Long id,
        String messageType,
        String messageContent,
        LocalDateTime createdAt
) {
    public static EumeChatContentResponse from(EumeChatContent content) {
        return new EumeChatContentResponse(
                content.getId(),
                content.getMessageType(),
                content.getMessageContent(),
                content.getCreatedAt()
        );
    }
}
