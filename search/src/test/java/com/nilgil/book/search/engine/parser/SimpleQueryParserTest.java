package com.nilgil.book.search.engine.parser;

import com.nilgil.book.search.engine.parser.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleQueryParserTest {

    private final QueryParser parser = new SimpleQueryParser();

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("빈 값이나 null 입력 시 EmptyQuery를 반환한다")
    void parseEmptyQuery(String input) {
        // when
        Query query = parser.parse(input);

        // then
        assertThat(query).isInstanceOf(EmptyQuery.class);
    }

    @Test
    @DisplayName("단일 검색어는 TermQuery로 파싱된다")
    void parseSingleTerm() {
        // given
        String input = "book";

        // when
        Query query = parser.parse(input);

        // then
        assertThat(query).isInstanceOfSatisfying(TermQuery.class, termQuery ->
                assertThat(termQuery.value()).isEqualTo("book"));
    }

    @Test
    @DisplayName("OR 연산자(|)로 구분된 검색어는 모두 SHOULD 조건으로 CompoundQuery에 포함된다")
    void parseOrQuery() {
        // given
        String input = "book|author";

        // when
        Query query = parser.parse(input);

        // then
        assertThat(query).isInstanceOfSatisfying(CompoundQuery.class, compoundQuery ->
                assertThat(compoundQuery.clauses())
                        .hasSize(2)
                        .extracting(Clause::occur)
                        .containsExactly(Occur.SHOULD, Occur.SHOULD));
    }

    @Test
    @DisplayName("NOT 연산자(-)로 구분된 검색어는 MUST와 MUST_NOT 조건으로 CompoundQuery에 포함된다")
    void parseNotQuery() {
        // given
        String input = "book-author";

        // when
        Query query = parser.parse(input);

        // then
        assertThat(query).isInstanceOfSatisfying(CompoundQuery.class, compoundQuery ->
                assertThat(compoundQuery.clauses())
                        .hasSize(2)
                        .extracting(Clause::occur)
                        .containsExactly(Occur.MUST, Occur.MUST_NOT));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  book  ", "  book  |  author  ", "  book  -  author  "})
    @DisplayName("앞뒤 공백이 포함된 TermQuery는 공백이 제거된 형태로 파싱된다")
    void parseNormalizedSingleTerm(String input) {
        // when
        Query query = parser.parse(input);

        // then
        if (query instanceof TermQuery(String value)) {

            assertThat(value).isEqualTo("book");

        } else if (query instanceof CompoundQuery(List<Clause> clauses)) {

            assertThat(clauses)
                    .hasSize(2)
                    .extracting(c -> ((TermQuery) c.query()).value())
                    .containsExactly("book", "author");

        }
    }
}