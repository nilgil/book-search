package com.nilgil.book.search.parser.model;

public record TermQuery(String value) implements Query {
    public TermQuery {
        if (value == null) {
            value = "";
        } else {
            value = value.trim();
        }
    }
}