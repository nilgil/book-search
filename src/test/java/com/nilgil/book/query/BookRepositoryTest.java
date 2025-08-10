package com.nilgil.book.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
class BookRepositoryTest {

    @Autowired
    BookRepository repository;

    @Autowired
    JdbcTemplate template;

    @Test
    @DisplayName("존재하는 ISBN 조회 시 성공적으로 도서를 조회한다")
    void save_and_findByIsbn13() {
        // given
        String sql = """
                insert into BOOK_READ_MODEL (isbn13, title, author, publisher, published_date, page_count) 
                                values ('9788991000155', '테스트 제목', '테스트 저자', '테스트 출판사', '2024-01-15', 320) 
                """;
        template.update(sql);

        // when
        Optional<BookRow> found = repository.findByIsbn13("9788991000155");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().title()).isEqualTo("테스트 제목");
    }

    @Test
    @DisplayName("존재하지 않는 ISBN 조회시 빈 값을 반환한다")
    void findByIsbn13_should_return_empty_when_not_exists() {
        // when
        Optional<BookRow> found = repository.findByIsbn13("9788991000155");

        // then
        assertThat(found).isEmpty();
    }
}