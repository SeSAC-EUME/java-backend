package com.project.eume.domain.dto.response;

public record EumeUserLogoutResponse (
    String message,
    boolean success
) {
    public static EumeUserLogoutResponse successed() {
        return new EumeUserLogoutResponse(
            "Successfully logged out",
            true
        );
    }
}
