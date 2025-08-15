package com.nilgil.book.core.search.executor;

import com.nilgil.book.core.search.executor.model.BookSearchResult;
import com.nilgil.book.core.search.planner.PlannedQuery;
import com.nilgil.book.core.share.PageReq;

public interface QueryExecutor {

    BookSearchResult execute(PlannedQuery query, PageReq pageReq, String rawQuery);

    String getEngineName();
}
