package com.nilgil.book.search.executor.model;

import com.nilgil.book.query.BookRow;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record BookHit(
        String isbn,
        String title,
        String subtitle,
        String image,
        String author,
        LocalDate published
) {
    public static BookHit from(BookRow row) {
        return BookHit.builder()
                .isbn(row.isbn13())
                .title(row.title())
                .subtitle(row.subtitle())
                .image(row.image())
                .author(row.author())
                .published(LocalDate.parse(row.publishedDate()))
                .build();
    }
}
