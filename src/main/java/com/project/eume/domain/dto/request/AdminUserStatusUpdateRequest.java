package com.project.eume.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AdminUserStatusUpdateRequest(
    @NotBlank(message = "상태 값은 필수입니다")
    @Pattern(regexp = "ACTIVE|DEACTIVATED", message = "상태 값은 ACTIVE 또는 DEACTIVATED만 가능합니다")
    String status
) {}
