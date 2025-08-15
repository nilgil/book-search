package com.nilgil.book.core.api.error;

import com.nilgil.book.share.ErrorType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BookError implements ErrorType {

    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "B001", "Book not found"),
    INVALID_ISBN(HttpStatus.BAD_REQUEST.value(), "B002", "Invalid ISBN"),
    ;

    private final int httpStatus;
    private final String errorCode;
    private final String message;

    BookError(int httpStatus, String errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }
}
