package com.project.eume.exceptions.errorcode;

import org.springframework.http.HttpStatus;

public enum AdminErrorCode implements ErrorCode {
    ADMIN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AD_0001", "존재하지 않는 관리자입니다"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "AD_0002", "비밀번호가 일치하지 않습니다"),
    DEACTIVATED_ADMIN(HttpStatus.FORBIDDEN, "AD_0003", "비활성화된 관리자 계정입니다"),
    ACCOUNT_LOCKED(HttpStatus.LOCKED, "AD_0004", "로그인 실패 횟수 초과로 계정이 잠겼습니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AD_0005", "사용자를 찾을 수 없습니다");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    AdminErrorCode(HttpStatus httpStatus, String errorCode, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }
}
