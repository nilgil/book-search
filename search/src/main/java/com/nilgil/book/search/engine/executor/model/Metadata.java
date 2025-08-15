package com.nilgil.book.search.engine.executor.model;

import com.nilgil.book.search.engine.planner.SearchStrategy;

public record Metadata(
        String engine,
        long executionTime,
        SearchStrategy strategy
) {
}
