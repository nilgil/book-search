package com.nilgil.book.search.keyword;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostgresSearchKeywordRepository {

    private final JdbcTemplate jdbcTemplate;

    public void incrementCount(String keyword) {
        jdbcTemplate.update(FTS_SEARCH_KEYWORD_INC_SQL, keyword);
    }

    private static final String FTS_SEARCH_KEYWORD_INC_SQL = """
                INSERT INTO popular_keywords (keyword, search_count, last_searched_at)
                VALUES (?, 1, now())
                ON CONFLICT (keyword)
                DO UPDATE SET search_count = popular_keywords.search_count + 1,
                              last_searched_at = now()
            """;
}
