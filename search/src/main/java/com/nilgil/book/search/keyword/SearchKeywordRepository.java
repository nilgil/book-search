package com.nilgil.book.search.keyword;

import com.nilgil.book.search.engine.parser.model.Query;

import java.util.Map;

public interface SearchKeywordRepository {

    void increment(Query query);

    Map<String, Long> getPopularKeywords();
}
