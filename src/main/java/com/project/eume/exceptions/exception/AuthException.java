package com.project.eume.exceptions.exception;

import com.project.eume.exceptions.errorcode.ErrorCode;

public class AuthException extends CommonException {
    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
