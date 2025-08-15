package com.nilgil.book.search.keyword;

import java.util.List;
import java.util.Set;

public interface SearchKeywordRepository {

    void increment(Set<String> keywords);

    List<KeywordRank> getPopularKeywords(int size);
}
