package com.project.eume.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminLoginRequest(
        @NotNull(message = "소속 기관은 필수입니다")
        Long sigunguId,

        @NotBlank(message = "로그인 ID는 필수입니다")
        String adminLoginId,

        @NotBlank(message = "비밀번호는 필수입니다")
        String adminPw
) {
}
