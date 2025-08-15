package com.nilgil.book.share;

import lombok.Getter;

@Getter
public enum CoreError implements ErrorType {

    INTERNAL_SERVER_ERROR(500, "C001", "Internal server error");

    private final int httpStatus;
    private final String errorCode;
    private final String message;

    CoreError(int httpStatus, String errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }
}
