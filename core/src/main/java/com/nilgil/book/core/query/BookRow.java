package com.nilgil.book.core.query;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("book_read_model")
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
