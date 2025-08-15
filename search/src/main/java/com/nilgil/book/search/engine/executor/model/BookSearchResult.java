package com.nilgil.book.search.engine.executor.model;


import com.nilgil.book.share.PageResponse;

import java.util.List;

public record BookSearchResult(
        PageResponse pageResponse,
        List<BookHit> bookHits,
        Metadata metadata
) {
    public BookSearchResult {
        bookHits = bookHits == null ? List.of() : bookHits;
    }
}
