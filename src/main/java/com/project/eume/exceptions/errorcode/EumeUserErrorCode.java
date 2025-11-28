package com.project.eume.exceptions.errorcode;

import org.springframework.http.HttpStatus;

public enum EumeUserErrorCode implements ErrorCode {
    NOT_EXIST(HttpStatus.NOT_FOUND, "U_0001", "존재하지 않는 유저입니다"),
    DEACTIVATED_USER(HttpStatus.FORBIDDEN, "U_0002", "비활성화된 사용자입니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U_0003", "이미 존재하는 이메일입니다"),
    ALREADY_DEACTIVATED(HttpStatus.CONFLICT, "U_0004", "이미 비활성화된 계정입니다"),
    WITHDRAWN_USER(HttpStatus.FORBIDDEN, "U_0005", "탈퇴한 사용자입니다");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    EumeUserErrorCode(HttpStatus httpStatus, String errorCode, String errorMessage) {
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
