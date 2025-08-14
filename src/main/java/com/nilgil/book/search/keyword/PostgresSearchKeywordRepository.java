package com.nilgil.book.search.keyword;

import com.nilgil.book.search.parser.model.Clause;
import com.nilgil.book.search.parser.model.CompoundQuery;
import com.nilgil.book.search.parser.model.Query;
import com.nilgil.book.search.parser.model.TermQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
@RequiredArgsConstructor
public class PostgresSearchKeywordRepository implements SearchKeywordRepository {

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
    public Map<String, Long> getPopularKeywords() {
        List<String> populars = jdbcTemplate.query(
                GET_POPULAR_KEYWORDS_SQL,
                (rs, rowNum) -> rs.getString("keyword")
        );
        return IntStream.range(0, populars.size())
                .boxed()
                .collect(Collectors.toMap(
                        populars::get,
                        i -> (long) i + 1,
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
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
                    popular_keywords
                ORDER BY
                    search_count DESC
                LIMIT 10;
            """;
}
