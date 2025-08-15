package com.nilgil.book.core.search.executor;

import com.nilgil.book.core.search.executor.model.BookHit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostgresFtsRepository {

    private final JdbcTemplate jdbcTemplate;

    public Page<BookHit> search(String ftsExpression, Pageable pageable) {
        List<BookHit> content = jdbcTemplate.query(
                FTS_SEARCH_SQL,
                MAPPER,
                ftsExpression,
                pageable.getPageSize(),
                pageable.getOffset()
        );

        Long total = jdbcTemplate.queryForObject(
                FTS_COUNT_SQL,
                Long.class,
                ftsExpression
        );

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private static final String FTS_SEARCH_SQL = """
            WITH fts_query AS (
                SELECT to_tsquery('korean', ?) AS query
            )
            SELECT
                brm.isbn13,
                brm.title,
                brm.subtitle,
                brm.image,
                brm.author,
                brm.published_date,
                ts_rank_cd(brm.search_vector, fq.query) AS rank
            FROM
                book_read_model brm, fts_query fq
            WHERE
                brm.search_vector @@ fq.query
            ORDER BY
                rank DESC
            LIMIT ? OFFSET ?
            """;

    private static final String FTS_COUNT_SQL = """
            WITH fts_query AS (
                SELECT to_tsquery('korean', ?) AS query
            )
            SELECT
                count(*)
            FROM
                book_read_model brm, fts_query fq
            WHERE
                brm.search_vector @@ fq.query
            """;

    private static final RowMapper<BookHit> MAPPER = (rs, rowNum) -> new BookHit(
            rs.getString("isbn13"),
            rs.getString("title"),
            rs.getString("subtitle"),
            rs.getString("image"),
            rs.getString("author"),
            rs.getString("published_date")
    );

}
