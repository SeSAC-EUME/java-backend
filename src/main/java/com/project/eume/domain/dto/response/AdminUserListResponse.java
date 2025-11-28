package com.project.eume.domain.dto.response;

import com.project.eume.domain.entity.EumeUser;
import org.springframework.data.domain.Page;

import java.util.List;

public record AdminUserListResponse(
        List<AdminUserResponse> users,
        int currentPage,
        int totalPages,
        long totalElements,
        boolean hasNext,
        boolean hasPrevious
) {
    public static AdminUserListResponse from(Page<EumeUser> page) {
        List<AdminUserResponse> users = page.getContent().stream()
                .map(AdminUserResponse::from)
                .toList();

        return new AdminUserListResponse(
                users,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}
