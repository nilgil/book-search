package com.nilgil.book.search.keyword;

import com.nilgil.book.search.engine.parser.model.Clause;
import com.nilgil.book.search.engine.parser.model.CompoundQuery;
import com.nilgil.book.search.engine.parser.model.Query;
import com.nilgil.book.search.engine.parser.model.TermQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PgSearchKeywordRepository implements SearchKeywordRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void increment(Query query) {
        switch (query) {
            case TermQuery tq -> jdbcTemplate.update(FTS_SEARCH_KEYWORD_INC_SQL, tq.value());
            case CompoundQuery cq -> cq.clauses().forEach(this::increment);
            default -> {
            }
        }
    }

    @Override
    public List<KeywordRank> getPopularKeywords(int size) {
        return jdbcTemplate.query(
                GET_POPULAR_KEYWORDS_SQL,
                (rs, rowNum) -> new KeywordRank(rowNum, rs.getString("keyword")),
                size
        );
    }

    private void increment(Clause clause) {
        increment(clause.query());
    }

    private static final String FTS_SEARCH_KEYWORD_INC_SQL = """
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
}
