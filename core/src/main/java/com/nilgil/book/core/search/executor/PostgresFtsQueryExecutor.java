package com.nilgil.book.core.search.executor;

import com.nilgil.book.core.search.executor.model.BookHit;
import com.nilgil.book.core.search.executor.model.BookSearchResult;
import com.nilgil.book.core.search.planner.PlannedQuery;
import com.nilgil.book.core.share.PageInfo;
import com.nilgil.book.core.share.PageReq;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Qualifier("primaryQueryExecutor")
@RequiredArgsConstructor
public class PostgresFtsQueryExecutor implements QueryExecutor {

    private final PostgresFtsExpressionBuilder expressionBuilder;
    private final PostgresFtsRepository repository;

    @Override
    public BookSearchResult execute(PlannedQuery query, PageReq pageReq, String rawQuery) {
        String ftsExpression = expressionBuilder.build(query);
        Pageable pageable = PageRequest.of(pageReq.page(), pageReq.size());

        Page<BookHit> queryResults = repository.search(ftsExpression, pageable);
        PageInfo pageInfo = getPageInfo(queryResults);

        return new BookSearchResult(pageInfo, queryResults.getContent(), null);
    }

    @Override
    public String getEngineName() {
        return "postgres-fts";
    }

    private static PageInfo getPageInfo(Page<BookHit> queryResults) {
        Pageable pageable = queryResults.getPageable();
        return new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
                queryResults.getTotalPages(), queryResults.getTotalElements());
    }
}
