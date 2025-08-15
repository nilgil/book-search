package com.nilgil.book.search.keyword;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class PgSearchKeywordRepository implements SearchKeywordRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void increment(Set<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate(INC_SEARCH_KEYWORD_SQL, keywords, 100,
                (PreparedStatement ps, String keyword) -> ps.setString(1, keyword));
    }

    @Override
    public List<KeywordRank> getPopularKeywords(int size) {
        return jdbcTemplate.query(
                GET_POPULAR_KEYWORDS_SQL,
                ROW_MAPPER,
                size
        );
    }

    private static final String INC_SEARCH_KEYWORD_SQL = """
                INSERT INTO search_keywords (keyword, search_count, last_searched_at)
                VALUES (?, 1, now())
                ON CONFLICT (keyword)
                DO UPDATE SET search_count = search_keywords.search_count + 1,
                              last_searched_at = now()
            """;

    public static final String GET_POPULAR_KEYWORDS_SQL = """
                SELECT
                    keyword,
                    search_count
                FROM
                    search_keywords
                ORDER BY
                    search_count DESC
                LIMIT ?;
            """;

    public static final RowMapper<KeywordRank> ROW_MAPPER = (rs, rowNum) ->
            new KeywordRank(rowNum + 1, rs.getString("keyword"));
}
