package com.nilgil.book.query;

import com.nilgil.book.share.Isbn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
}