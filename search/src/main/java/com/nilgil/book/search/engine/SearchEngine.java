package com.nilgil.book.search.engine;

import com.nilgil.book.search.engine.executor.QueryExecutor;
import com.nilgil.book.search.engine.executor.model.BookSearchResult;
import com.nilgil.book.search.engine.parser.QueryParser;
import com.nilgil.book.search.engine.parser.model.Query;
import com.nilgil.book.search.engine.planner.QueryPlanner;
import com.nilgil.book.search.engine.planner.SearchStrategy;
import com.nilgil.book.share.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchEngine {

    private final QueryParser parser;
    private final QueryPlanner planner;
    private final QueryExecutor executor;
    private final SearchedEventPublisher searchedEventPublisher;

    public BookSearchResult search(String rawQuery, PageRequest pageRequest) {
        Query query = parser.parse(rawQuery);
        searchedEventPublisher.publish(query);

        SearchStrategy strategy = planner.plan(query);
        return executor.execute(query, strategy, pageRequest, rawQuery);
    }
}
