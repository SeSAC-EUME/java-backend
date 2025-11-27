package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.EumeChatContent;

public record EumeChatContentCreateResponse(
        EumeChatContentResponse userMessage,
        EumeChatContentResponse eumeMessage
) {
    public static EumeChatContentCreateResponse from(
            EumeChatContent userContent,
            EumeChatContent eumeContent
    ) {
        return new EumeChatContentCreateResponse(
                EumeChatContentResponse.from(userContent),
                EumeChatContentResponse.from(eumeContent)
        );
    }
}
