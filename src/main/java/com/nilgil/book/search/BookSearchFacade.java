package com.nilgil.book.search;

import com.nilgil.book.search.executor.QueryExecutor;
import com.nilgil.book.search.executor.model.BookSearchResult;
import com.nilgil.book.search.keyword.KeywordSearchedEvent;
import com.nilgil.book.search.parser.QueryParser;
import com.nilgil.book.search.parser.model.Query;
import com.nilgil.book.search.planner.PlannedQuery;
import com.nilgil.book.search.planner.QueryPlanner;
import com.nilgil.book.share.PageReq;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookSearchFacade {

    private final QueryParser parser;
    private final QueryPlanner planner;
    private final QueryExecutor executor;
    private final ApplicationEventPublisher publisher;

    public BookSearchResult search(String rawQuery, PageReq pageReq) {
        Query query = parser.parse(rawQuery);
        publisher.publishEvent(new KeywordSearchedEvent(query));

        PlannedQuery plannedQuery = planner.plan(query);
        return executor.execute(plannedQuery, pageReq, rawQuery);
    }
}
