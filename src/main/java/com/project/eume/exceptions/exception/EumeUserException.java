package com.project.eume.exceptions.exception;

import com.project.eume.exceptions.errorcode.ErrorCode;

public class EumeUserException extends CommonException {
    public EumeUserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
