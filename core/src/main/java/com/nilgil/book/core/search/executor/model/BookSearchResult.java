package com.nilgil.book.core.search.executor.model;

import com.nilgil.book.core.share.PageInfo;

import java.util.List;

public record BookSearchResult(
        PageInfo pageInfo,
        List<BookHit> bookHits,
        Metadata metadata
) {
    public BookSearchResult {
        bookHits = bookHits == null ? List.of() : bookHits;
    }
}
