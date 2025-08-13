package com.nilgil.book.search.executor;

import com.nilgil.book.search.executor.model.BookSearchResult;
import com.nilgil.book.search.planner.PlannedQuery;
import com.nilgil.book.share.PageReq;

public interface QueryExecutor {

    BookSearchResult execute(PlannedQuery query, PageReq pageReq, String rawQuery);

    String getEngineName();
}
