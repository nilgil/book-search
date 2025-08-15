package com.nilgil.book.core.search.executor;

import com.nilgil.book.core.search.executor.model.BookSearchResult;
import com.nilgil.book.core.search.executor.model.Metadata;
import com.nilgil.book.core.search.planner.PlannedQuery;
import com.nilgil.book.share.PageReq;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@RequiredArgsConstructor
public class MetadataQueryExecutorDecorator implements QueryExecutor {

    private final QueryExecutor primaryQueryExecutor;

    @Override
    public BookSearchResult execute(PlannedQuery query, PageReq pageReq, String rawQuery) {
        long startTime = System.currentTimeMillis();

        BookSearchResult original = primaryQueryExecutor.execute(query, pageReq, rawQuery);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        Metadata metadata = createMetadata(query, executionTime);

        return createSearchResult(original, metadata);
    }

    @Override
    public String getEngineName() {
        return primaryQueryExecutor.getEngineName();
    }

    private Metadata createMetadata(PlannedQuery query, long executionTime) {
        return new Metadata(
                getEngineName(),
                executionTime,
                query.strategy()
        );
    }

    private BookSearchResult createSearchResult(BookSearchResult original, Metadata metadata) {
        return new BookSearchResult(
                original.pageInfo(),
                original.bookHits(),
                metadata
        );
    }
}
