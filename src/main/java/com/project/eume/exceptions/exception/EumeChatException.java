package com.project.eume.exceptions.exception;

import com.project.eume.exceptions.errorcode.ErrorCode;

public class EumeChatException extends CommonException {
    public EumeChatException(ErrorCode errorCode) {
        super(errorCode);
    }
}
