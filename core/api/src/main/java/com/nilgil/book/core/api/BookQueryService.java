package com.nilgil.book.core.api;

import com.nilgil.book.core.domain.BookRepository;
import com.nilgil.book.core.domain.Isbn;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookQueryService {

    private final BookRepository bookRepository;

    public BookDetailResponse getDetailByIsbn(Isbn isbn) {
        return bookRepository.findByIsbn13(isbn.asIsbn13())
                .map(BookDetailResponse::from)
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }
}
