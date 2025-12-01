package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.UserChatContent;

public record UserChatContentCreateResponse(
        UserChatContentResponse userMessage,
        UserChatContentResponse eumeMessage
) {
    public static UserChatContentCreateResponse from(
            UserChatContent userContent,
            UserChatContent eumeContent
    ) {
        return new UserChatContentCreateResponse(
                UserChatContentResponse.from(userContent),
                UserChatContentResponse.from(eumeContent)
        );
    }

    /**
     * n8n에서 메시지 저장을 담당하는 경우, 응답 문자열만으로 생성
     */
    public static UserChatContentCreateResponse fromAiResponse(String userMessage, String eumeResponse) {
        return new UserChatContentCreateResponse(
                new UserChatContentResponse(null, null, null, "USER", userMessage, null),
                new UserChatContentResponse(null, null, null, "EUME", eumeResponse, null)
        );
    }
}
