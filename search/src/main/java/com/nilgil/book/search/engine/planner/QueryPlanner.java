package com.nilgil.book.search.engine.planner;

import com.nilgil.book.search.engine.parser.model.Query;

public interface QueryPlanner {
    SearchStrategy plan(Query query);
}
