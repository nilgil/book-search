package com.nilgil.book.search.engine.parser;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum Operator {
    OR('|'),
    NOT('-');

    private final char symbol;

    Operator(char symbol) {
        this.symbol = symbol;
    }

    public static Optional<Operator> find(char symbol) {
        for (Operator op : Operator.values()) {
            if (op.getSymbol() == symbol) {
                return Optional.of(op);
            }
        }
        return Optional.empty();
    }
}
