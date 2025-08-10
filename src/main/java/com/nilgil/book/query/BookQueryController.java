package com.nilgil.book.query;

import com.nilgil.book.share.Isbn;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookQueryController {

    private final BookReadService bookReadService;

    @GetMapping("/{isbn13}")
    BookDetailResponse getDetailByIsbn(@PathVariable String isbn13) {
        Isbn isbn = new Isbn(isbn13);
        return bookReadService.getDetailByIsbn(isbn);
    }
}
