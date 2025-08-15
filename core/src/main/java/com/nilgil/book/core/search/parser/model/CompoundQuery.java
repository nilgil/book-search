package com.nilgil.book.core.search.parser.model;

import java.util.List;

public record CompoundQuery(
        List<Clause> clauses
) implements Query {
    public CompoundQuery {
        clauses = clauses == null ? List.of() : clauses;
    }
}
