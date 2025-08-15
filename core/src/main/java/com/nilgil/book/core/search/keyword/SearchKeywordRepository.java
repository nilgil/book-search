package com.nilgil.book.core.search.keyword;

import com.nilgil.book.core.search.parser.model.Query;

import java.util.Map;

public interface SearchKeywordRepository {

    void increment(Query query);

    Map<String, Long> getPopularKeywords();
}
