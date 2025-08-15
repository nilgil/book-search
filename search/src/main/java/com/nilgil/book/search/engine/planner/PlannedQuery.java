package com.nilgil.book.search.engine.planner;

import com.nilgil.book.search.engine.parser.model.EmptyQuery;
import com.nilgil.book.search.engine.parser.model.Query;

public record PlannedQuery(SearchStrategy strategy, Query query) {

    public PlannedQuery {
        strategy = strategy == null ? SearchStrategy.NONE : strategy;
        query = query == null ? EmptyQuery.INSTANCE : query;
    }
}
