package com.project.eume.exceptions.errorcode;

import org.springframework.http.HttpStatus;

public enum UserChatErrorCode implements ErrorCode {
    EMPTY_ROOM_TITLE(HttpStatus.BAD_REQUEST, "UC_0001", "채팅방 제목이 비어있습니다"),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "UC_0002", "채팅방을 찾을 수 없습니다"),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "UC_0003", "채팅방 접근 권한이 없습니다"),
    INVALID_PAGE_NUMBER(HttpStatus.BAD_REQUEST, "UC_0004", "페이지 번호는 0 이상이어야 합니다"),
    INVALID_PAGE_SIZE(HttpStatus.BAD_REQUEST, "UC_0005", "페이지 크기는 1 이상이어야 합니다"),
    EMPTY_MESSAGE_CONTENT(HttpStatus.BAD_REQUEST, "UC_0006", "메시지 내용이 비어있습니다"),
    N8N_WEBHOOK_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UC_0007", "AI 서비스 호출에 실패했습니다");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    UserChatErrorCode(HttpStatus httpStatus, String errorCode, String errorMessage) {
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
