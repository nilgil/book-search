package com.nilgil.book.core.query;

import com.nilgil.book.search.engine.SearchEngine;
import com.nilgil.book.search.engine.executor.model.BookSearchResult;
import com.nilgil.book.share.PageReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Books", description = "도서 조회 및 검색 API")
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookQueryController {

    private final BookReadService bookReadService;
    private final SearchEngine searchEngine;

    @Operation(
            summary = "ISBN으로 도서 상세 조회",
            description = "ISBN-13으로 특정 도서의 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = BookDetailResponse.class))),
                    @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음")
            }
    )
    @GetMapping("/{isbn13}")
    BookDetailResponse getDetailByIsbn(
            @Parameter(description = "ISBN-13", example = "9788991000155")
            @PathVariable String isbn13) {
        Isbn isbn = new Isbn(isbn13);
        return bookReadService.getDetailByIsbn(isbn);
    }

    @Operation(
            summary = "검색어로 도서 검색",
            description = "검색어(q)에 해당하는 도서를 페이지네이션하여 조회합니다.",
            responses = @ApiResponse(responseCode = "200", description = "검색 성공", content = @Content(schema = @Schema(implementation = BookSearchResponse.class)))
    )
    @GetMapping("/search")
    BookSearchResponse searchByQuery(
            @Parameter(description = "검색 질의", example = "java|스프링")
            @RequestParam String q,
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        PageReq pageReq = new PageReq(page, size);
        BookSearchResult result = searchEngine.search(q, pageReq);
        return BookSearchResponse.from(q, result);
    }

    @Operation(
            summary = "인기 검색어 Top 10",
            description = "최근 집계된 인기 검색어 상위 10개를 조회합니다.",
            responses = @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = PopularKeywordsResponse.class)))
    )
    @GetMapping("/search/top10")
    PopularKeywordsResponse getPopularKeywords() {
        return new PopularKeywordsResponse(searchEngine.getPopularKeywords());
    }
}
