package com.nilgil.book.domain;

import java.time.LocalDate;

public record Book(
        Isbn isbn,
        String title,
        String subtitle,
        String description,
        String image,
        String author,
        String translator,
        String publisher,
        LocalDate publishedDate,
        Integer pageCount
) {
}
