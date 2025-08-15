package com.nilgil.book.core.share;

public record PageInfo(
        int page,
        int size,
        int totalPages,
        long totalElements
) {
}
