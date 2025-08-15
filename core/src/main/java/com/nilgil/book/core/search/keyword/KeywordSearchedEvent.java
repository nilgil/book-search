package com.nilgil.book.core.search.keyword;

import com.nilgil.book.core.search.parser.model.Query;

public record KeywordSearchedEvent(Query parsedQuery) {
}
