package com.nilgil.book.search.executor.model;

import com.nilgil.book.search.planner.SearchStrategy;

public record Metadata(
        String engine,
        long executionTime,
        SearchStrategy strategy
) {
}
