package com.nilgil.book.share;

public record PageInfo(
        int page,
        int size,
        int totalPages,
        long totalElements
) {
}
