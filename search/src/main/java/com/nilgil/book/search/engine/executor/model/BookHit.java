package com.nilgil.book.search.engine.executor.model;

import lombok.Builder;

@Builder
public record BookHit(
        String isbn,
        String title,
        String subtitle,
        String image,
        String author,
        String published
) {
}
