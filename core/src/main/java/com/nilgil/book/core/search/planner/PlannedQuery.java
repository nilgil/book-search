package com.nilgil.book.core.search.planner;

import com.nilgil.book.core.search.parser.model.EmptyQuery;
import com.nilgil.book.core.search.parser.model.Query;

public record PlannedQuery(SearchStrategy strategy, Query query) {

    public PlannedQuery {
        strategy = strategy == null ? SearchStrategy.NONE : strategy;
        query = query == null ? EmptyQuery.INSTANCE : query;
    }
}
