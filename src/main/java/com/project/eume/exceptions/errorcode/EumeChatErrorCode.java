package com.project.eume.exceptions.errorcode;

import org.springframework.http.HttpStatus;

public enum EumeChatErrorCode implements ErrorCode {
    CHAT_LIST_ALREADY_EXISTS(HttpStatus.CONFLICT, "EC_0001", "이미 채팅 목록이 존재합니다"),
    CHAT_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "EC_0002", "채팅 목록을 찾을 수 없습니다"),
    INVALID_PAGE_NUMBER(HttpStatus.BAD_REQUEST, "EC_0003", "페이지 번호는 0 이상이어야 합니다"),
    INVALID_PAGE_SIZE(HttpStatus.BAD_REQUEST, "EC_0004", "페이지 크기는 1 이상이어야 합니다"),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "EC_0005", "접근 권한이 없습니다"),
    EMPTY_MESSAGE_CONTENT(HttpStatus.BAD_REQUEST, "EC_0006", "메시지 내용이 비어있습니다"),
    N8N_WEBHOOK_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "EC_0007", "AI 응답 생성에 실패했습니다");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    EumeChatErrorCode(HttpStatus httpStatus, String errorCode, String errorMessage) {
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
