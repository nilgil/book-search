package com.nilgil.book.query;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("BOOK_READ_MODEL")
public record BookRow(
        @Id
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
}
