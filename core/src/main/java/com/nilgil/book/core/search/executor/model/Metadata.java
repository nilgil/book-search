package com.nilgil.book.core.search.executor.model;

import com.nilgil.book.core.search.planner.SearchStrategy;

public record Metadata(
        String engine,
        long executionTime,
        SearchStrategy strategy
) {
}
