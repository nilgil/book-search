package com.nilgil.book.core.api.error;


import com.nilgil.book.core.domain.Isbn;
import com.nilgil.book.share.CoreException;
import lombok.Getter;

@Getter
public class BookNotFoundException extends CoreException {

    private final Isbn isbn;

    public BookNotFoundException(Isbn isbn, Throwable cause) {
        super(BookError.BOOK_NOT_FOUND, "Book not found: " + isbn.asIsbn13(), cause);
        this.isbn = isbn;
    }

    public BookNotFoundException(Isbn isbn) {
        this(isbn, null);
    }
}