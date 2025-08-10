package com.nilgil.book.query;

import com.nilgil.book.share.Isbn;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookReadService {

    private final BookRepository bookRepository;

    public BookDetailResponse getDetailByIsbn(Isbn isbn) {
        return bookRepository.findByIsbn13(isbn.asIsbn13())
                .map(BookDetailResponse::from)
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }
}
