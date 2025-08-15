package com.nilgil.book.core.query;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidIsbnException extends RuntimeException {
    public InvalidIsbnException(String message) {
        super(message);
    }
}
