package com.nilgil.book.search.keyword;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchKeywordService {

    private final SearchKeywordRepository searchKeywordRepository;

    public List<KeywordRank> getPopularKeywords(int size) {
        return searchKeywordRepository.getPopularKeywords(size);
    }
}
