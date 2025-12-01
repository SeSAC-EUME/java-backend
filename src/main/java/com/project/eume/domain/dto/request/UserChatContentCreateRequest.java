package com.project.eume.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserChatContentCreateRequest(
        @NotBlank(message = "메시지 내용은 필수입니다")
        String messageContent
) {
}
