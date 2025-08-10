package com.nilgil.book.query;

import com.nilgil.book.share.Isbn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class BookReadServiceTest {

    @Autowired
    private BookReadService bookReadService;

    @Autowired
    private JdbcTemplate template;

    @Test
    @DisplayName("ISBN으로 도서 상세 조회 성공")
    void getDetailByIsbn_success() {
        String sql = """
                insert into BOOK_READ_MODEL (isbn13, title, author, publisher, published_date, page_count) 
                                values ('9788991000155', '테스트 제목', '테스트 저자', '테스트 출판사', '2024-01-15', 320) 
                """;
        template.update(sql);

        Isbn isbn = new Isbn("9788991000155");

        // when
        BookDetailResponse response = bookReadService.getDetailByIsbn(isbn);

        // then
        assertThat(response.isbn13()).isEqualTo("9788991000155");
        assertThat(response.title()).isEqualTo("테스트 제목");
    }

    @Test
    @DisplayName("존재하지 않는 ISBN 조회시 예외 발생")
    void getDetailByIsbn_notFound() {
        Isbn isbn = new Isbn("9788991000155");

        // then
        assertThatThrownBy(() -> bookReadService.getDetailByIsbn(isbn))
                .isInstanceOf(BookNotFoundException.class);
    }
}