package com.project.eume.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdminLoginRequest(
        @NotBlank(message = "로그인 ID는 필수입니다")
        String adminLoginId,

        @NotBlank(message = "비밀번호는 필수입니다")
        String adminPw
) {
}
