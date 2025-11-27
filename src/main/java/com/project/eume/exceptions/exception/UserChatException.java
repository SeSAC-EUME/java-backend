package com.project.eume.exceptions.exception;

import com.project.eume.exceptions.errorcode.ErrorCode;

public class UserChatException extends CommonException {
    public UserChatException(ErrorCode errorCode) {
        super(errorCode);
    }
}
