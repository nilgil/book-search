package com.nilgil.book.query;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BookRepository extends CrudRepository<BookRow, String> {
    Optional<BookRow> findByIsbn13(String isbn13);
}
