package com.project.eume.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminRegisterRequest(
        @NotNull(message = "소속 기관은 필수입니다")
        Long sigunguId,

        @NotBlank(message = "로그인 ID는 필수입니다")
        @Size(min = 4, max = 20, message = "로그인 ID는 4~20자여야 합니다")
        String adminLoginId,

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, max = 100, message = "비밀번호는 8자 이상이어야 합니다")
        String adminPw,

        @NotBlank(message = "관리자 이름은 필수입니다")
        String adminName,

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String adminEmail,

        String adminPhone
) {
}
