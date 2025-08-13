package com.nilgil.book.search.planner;

import com.nilgil.book.search.parser.model.Query;

public record PlannedQuery(SearchStrategy strategy, Query query) {
}
