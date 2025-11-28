package com.project.eume.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserChatListCreateRequest(
        @NotBlank(message = "채팅방 제목은 필수입니다")
        @Size(max = 100, message = "채팅방 제목은 100자 이내여야 합니다")
        String roomTitle
) {
}
