package com.nilgil.book.core.api;

import com.nilgil.book.core.domain.BookRow;
import lombok.Builder;

@Builder
public record BookDetailResponse(
        String isbn13,
        String title,
        String subtitle,
        String description,
        String image,
        String author,
        String translator,
        String publisher,
        String publishedDate,
        String pageCount
) {
    public static BookDetailResponse from(BookRow row) {
        return BookDetailResponse.builder()
                .isbn13(row.isbn13())
                .title(row.title())
                .subtitle(row.subtitle())
                .description(row.description())
                .image(row.image())
                .author(row.author())
                .translator(row.translator())
                .publisher(row.publisher())
                .publishedDate(row.publishedDate())
                .pageCount(row.pageCount())
                .build();
    }
}
