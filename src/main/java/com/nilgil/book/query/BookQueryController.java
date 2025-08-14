package com.nilgil.book.query;

import com.nilgil.book.search.BookSearchFacade;
import com.nilgil.book.search.executor.model.BookSearchResult;
import com.nilgil.book.share.Isbn;
import com.nilgil.book.share.PageReq;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookQueryController {

    private final BookReadService bookReadService;
    private final BookSearchFacade bookSearchFacade;

    @GetMapping("/{isbn13}")
    BookDetailResponse getDetailByIsbn(@PathVariable String isbn13) {
        Isbn isbn = new Isbn(isbn13);
        return bookReadService.getDetailByIsbn(isbn);
    }

    @GetMapping("/search")
    BookSearchResponse searchByQuery(@RequestParam String q,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size
    ) {
        PageReq pageReq = new PageReq(page, size);
        BookSearchResult result = bookSearchFacade.search(q, pageReq);
        return BookSearchResponse.from(q, result);
    }

    @GetMapping("/search/top10")
    PopularKeywordsResponse getPopularKeywords() {
        return new PopularKeywordsResponse(bookSearchFacade.getPopularKeywords());
    }
}
