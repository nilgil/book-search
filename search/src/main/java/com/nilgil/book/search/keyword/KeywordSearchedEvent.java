package com.nilgil.book.search.keyword;

import com.nilgil.book.search.engine.parser.model.Query;

public record KeywordSearchedEvent(Query parsedQuery) {
}
