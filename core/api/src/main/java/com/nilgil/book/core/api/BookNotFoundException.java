package com.nilgil.book.core.api;


import com.nilgil.book.core.domain.Isbn;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class BookNotFoundException extends RuntimeException {
    private final Isbn isbn;

    public BookNotFoundException(Isbn isbn) {
        super("Book not found: " + isbn.asIsbn13());
        this.isbn = isbn;
    }

    public Isbn isbn() {
        return isbn;
    }
}