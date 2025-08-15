package com.nilgil.book.search.engine.executor;

import com.nilgil.book.search.engine.executor.model.BookSearchResult;
import com.nilgil.book.search.engine.parser.model.Query;
import com.nilgil.book.search.engine.planner.SearchStrategy;
import com.nilgil.book.share.PageRequest;

public interface QueryExecutor {

    BookSearchResult execute(Query query, SearchStrategy strategy, PageRequest pageRequest, String rawQuery);

    String getEngineName();
}
