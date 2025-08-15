package com.nilgil.book.core.query;

import com.nilgil.book.search.keyword.KeywordRank;

public record KeywordRankResponse(int rank, String keyword) {
    public static KeywordRankResponse from(KeywordRank keywordRank) {
        return new KeywordRankResponse(keywordRank.rank(), keywordRank.keyword());
    }
}
