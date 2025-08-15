package com.nilgil.book.core.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BookRepository extends CrudRepository<BookRow, String> {
    Optional<BookRow> findByIsbn13(String isbn13);
}
