package com.nilgil.book.core.query;

import com.nilgil.book.core.search.BookSearchFacade;
import com.nilgil.book.core.search.executor.model.BookHit;
import com.nilgil.book.core.search.executor.model.BookSearchResult;
import com.nilgil.book.core.search.executor.model.Metadata;
import com.nilgil.book.core.search.planner.SearchStrategy;
import com.nilgil.book.core.share.Isbn;
import com.nilgil.book.core.share.PageInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookQueryController.class)
class BookQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookReadService bookReadService;

    @MockitoBean
    private BookSearchFacade bookSearchFacade;

    @Test
    @DisplayName("ISBN으로 도서 상세 조회 성공")
    void getDetailByIsbn_success() throws Exception {
        // given
        String isbn = "9788991000155";
        BookDetailResponse response = BookDetailResponse.builder()
                .isbn13(isbn)
                .title("테스트 제목")
                .author("테스트 저자")
                .publisher("테스트 출판사")
                .publishedDate("2024-01-15")
                .pageCount("320")
                .build();
        given(bookReadService.getDetailByIsbn(any(Isbn.class))).willReturn(response);

        // when, then
        mockMvc.perform(get("/books/" + isbn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn13").value(isbn))
                .andExpect(jsonPath("$.title").value("테스트 제목"));
    }

    @Test
    @DisplayName("존재하지 않는 ISBN 조회시 404 응답")
    void getDetailByIsbn_notFound() throws Exception {
        // given
        Isbn isbn = new Isbn("9788991000155");
        given(bookReadService.getDetailByIsbn(isbn))
                .willThrow(new BookNotFoundException(isbn));

        // when, then
        mockMvc.perform(get("/books/9788991000155"))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("검색어로 도서 검색 성공")
    void searchByQuery_success() throws Exception {
        // given
        String query = "테스트|hi";
        Isbn isbn = new Isbn("9788991000155");

        BookSearchResult result = new BookSearchResult(
                new PageInfo(1, 10, 1, 8),
                List.of(new BookHit(isbn.asIsbn13(), "테스트 제목", "부제목", "이미지URL", "저자", "2024-01-15")),
                new Metadata(query, 100L, SearchStrategy.SINGLE_TERM)
        );

        given(bookSearchFacade.search(any(), any())).willReturn(result);

        // when, then
        mockMvc.perform(get("/books/search")
                        .param("q", query)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchQuery").value(query))
                .andExpect(jsonPath("$.books[0].isbn").value(isbn.asIsbn13()))
                .andExpect(jsonPath("$.books[0].title").value("테스트 제목"))
                .andExpect(jsonPath("$.books[0].author").value("저자"));
    }

    @Test
    @DisplayName("검색 결과가 없을 경우 빈 목록 반환")
    void searchByQuery_emptyResult() throws Exception {
        // given
        String query = "존재하지않는검색어";
        BookSearchResult result = new BookSearchResult(
                new PageInfo(0, 10, 0, 0),
                List.of(),
                new Metadata(query, 50L, SearchStrategy.SINGLE_TERM)
        );
        given(bookSearchFacade.search(any(), any())).willReturn(result);

        // when, then
        mockMvc.perform(get("/books/search")
                        .param("q", query)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchQuery").value(query))
                .andExpect(jsonPath("$.books").isArray())
                .andExpect(jsonPath("$.books").isEmpty());
    }

}