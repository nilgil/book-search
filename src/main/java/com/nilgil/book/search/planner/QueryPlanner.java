package com.nilgil.book.search.planner;

import com.nilgil.book.search.parser.model.Query;

public interface QueryPlanner {
    PlannedQuery plan(Query query);
}
