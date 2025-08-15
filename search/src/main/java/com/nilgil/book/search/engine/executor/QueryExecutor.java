package com.nilgil.book.search.engine.executor;

import com.nilgil.book.search.engine.executor.model.BookSearchResult;
import com.nilgil.book.search.engine.planner.PlannedQuery;
import com.nilgil.book.share.PageReq;

public interface QueryExecutor {

    BookSearchResult execute(PlannedQuery query, PageReq pageReq, String rawQuery);

    String getEngineName();
}
