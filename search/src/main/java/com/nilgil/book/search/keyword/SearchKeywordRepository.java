package com.nilgil.book.search.keyword;

import com.nilgil.book.search.engine.parser.model.Query;

import java.util.List;

public interface SearchKeywordRepository {

    void increment(Query query);

    List<KeywordRank> getPopularKeywords(int size);
}
