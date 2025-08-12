package com.nilgil.book.search.parser.model;

public class EmptyQuery implements Query {
    public static final EmptyQuery INSTANCE = new EmptyQuery();

    private EmptyQuery() {
    }
}
