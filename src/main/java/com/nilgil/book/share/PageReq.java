package com.nilgil.book.share;

public record PageReq(
        int page,
        int size
) {
    public PageReq {
        page = Math.max(page, 0);
        size = Math.max(size, 1);
        size = Math.min(size, 100);
    }
}
