package com.nilgil.book.search.keyword;

import com.nilgil.book.search.parser.model.Query;

public record KeywordSearchedEvent(Query parsedQuery) {
}
