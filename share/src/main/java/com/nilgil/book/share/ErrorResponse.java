package com.nilgil.book.share;

public record ErrorResponse(
        String errorCode,
        String message
) {
}
