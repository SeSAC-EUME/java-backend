package com.project.eume.exceptions.exception;

import com.project.eume.exceptions.errorcode.ErrorCode;

public class AdminException extends CommonException {
    public AdminException(ErrorCode errorCode) {
        super(errorCode);
    }
}
