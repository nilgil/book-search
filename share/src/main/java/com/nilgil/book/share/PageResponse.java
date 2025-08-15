package com.nilgil.book.share;

public record PageResponse(
        int page,
        int size,
        int totalPages,
        long totalElements
) {
}
