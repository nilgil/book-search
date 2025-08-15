package com.nilgil.book.share;

public record PageResponse(
        int page,
        int size,
        int totalPages,
        long totalElements
) {
    public static final PageResponse EMPTY = new PageResponse(0, 0, 0, 0);
}
