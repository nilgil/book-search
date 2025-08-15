package com.nilgil.book.search.engine.executor;

import com.nilgil.book.search.engine.executor.model.BookHit;
import com.nilgil.book.search.engine.executor.model.BookSearchResult;
import com.nilgil.book.search.engine.planner.PlannedQuery;
import com.nilgil.book.share.PageRequest;
import com.nilgil.book.share.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Qualifier("primaryQueryExecutor")
@RequiredArgsConstructor
public class PgFtsQueryExecutor implements QueryExecutor {

    private final PgFtsExpressionBuilder expressionBuilder;
    private final PgFtsRepository repository;

    @Override
    public BookSearchResult execute(PlannedQuery query, PageRequest pageRequest, String rawQuery) {
        String ftsExpression = expressionBuilder.build(query);
        Pageable pageable = org.springframework.data.domain.PageRequest.of(pageRequest.page(), pageRequest.size());

        Page<BookHit> queryResults = repository.search(ftsExpression, pageable);
        PageResponse pageResponse = getPageInfo(queryResults);

        return new BookSearchResult(pageResponse, queryResults.getContent(), null);
    }

    @Override
    public String getEngineName() {
        return "postgres-fts";
    }

    private static PageResponse getPageInfo(Page<BookHit> queryResults) {
        Pageable pageable = queryResults.getPageable();
        return new PageResponse(pageable.getPageNumber(), pageable.getPageSize(),
                queryResults.getTotalPages(), queryResults.getTotalElements());
    }
}
