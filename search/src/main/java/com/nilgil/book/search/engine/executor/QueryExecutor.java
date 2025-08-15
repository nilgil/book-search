package com.nilgil.book.search.engine.executor;

import com.nilgil.book.search.engine.executor.model.BookSearchResult;
import com.nilgil.book.search.engine.planner.PlannedQuery;
import com.nilgil.book.share.PageRequest;

public interface QueryExecutor {

    BookSearchResult execute(PlannedQuery query, PageRequest pageRequest, String rawQuery);

    String getEngineName();
}
