package com.nilgil.book.core.search.planner;

import com.nilgil.book.core.search.parser.model.Query;

public interface QueryPlanner {
    PlannedQuery plan(Query query);
}
