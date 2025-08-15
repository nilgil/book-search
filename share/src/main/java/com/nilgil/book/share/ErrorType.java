package com.nilgil.book.share;

public interface ErrorType {
    int getHttpStatus();

    String getErrorCode();

    String getMessage();
}
