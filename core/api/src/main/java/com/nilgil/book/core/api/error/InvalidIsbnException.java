package com.nilgil.book.core.api.error;

import com.nilgil.book.share.CoreException;
import lombok.Getter;

@Getter
public class InvalidIsbnException extends CoreException {

    private final String input;

    public InvalidIsbnException(String input, Throwable cause) {
        super(BookError.INVALID_ISBN, cause.getMessage(), cause);
        this.input = input;
    }

    public InvalidIsbnException(String input) {
        this(input, null);
    }
}
