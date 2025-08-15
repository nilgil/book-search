package com.nilgil.book.search.engine.executor;

import com.nilgil.book.search.engine.executor.model.BookSearchResult;
import com.nilgil.book.search.engine.executor.model.Metadata;
import com.nilgil.book.search.engine.parser.model.Query;
import com.nilgil.book.search.engine.planner.SearchStrategy;
import com.nilgil.book.share.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@RequiredArgsConstructor
public class DecorateMetadataQueryExecutor implements QueryExecutor {

    private final QueryExecutor primaryQueryExecutor;

    @Override
    public BookSearchResult execute(Query query, SearchStrategy strategy, PageRequest pageRequest, String rawQuery) {
        long startTime = System.currentTimeMillis();

        BookSearchResult original = primaryQueryExecutor.execute(query, strategy, pageRequest, rawQuery);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        Metadata metadata = createMetadata(query, strategy, executionTime);

        return createSearchResult(original, metadata);
    }

    @Override
    public String getEngineName() {
        return primaryQueryExecutor.getEngineName();
    }

    private Metadata createMetadata(Query query, SearchStrategy strategy, long executionTime) {
        return new Metadata(
                getEngineName(),
                executionTime,
                strategy
        );
    }

    private BookSearchResult createSearchResult(BookSearchResult original, Metadata metadata) {
        return new BookSearchResult(
                original.pageResponse(),
                original.bookHits(),
                metadata
        );
    }
}
