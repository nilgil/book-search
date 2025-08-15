package com.nilgil.book.search.engine.parser;

import com.nilgil.book.search.engine.parser.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SimpleQueryParser implements QueryParser {

    @Override
    public Query parse(String rawQuery) {
        if (rawQuery == null || rawQuery.isBlank()) {
            return EmptyQuery.INSTANCE;
        }

        ParserState state = new ParserState(rawQuery);

        return parseExpression(state);
    }

    private Query parseExpression(ParserState state) {
        Query left = parseTermQuery(state);

        while (!state.isEof()) {
            state.consumeWhitespace();
            if (state.isEof()) {
                break;
            }

            Optional<Operator> operatorOpt = Operator.find(state.peek());
            if (operatorOpt.isEmpty()) {
                break;
            }

            state.consume();
            Query right = parseTermQuery(state);
            left = buildBooleanQuery(left, right, operatorOpt.get());
        }
        return left;
    }

    private Query parseTermQuery(ParserState state) {
        state.consumeWhitespace();
        String term = readTerm(state);
        if (term.isBlank()) {
            return EmptyQuery.INSTANCE;
        }
        return new TermQuery(term);
    }

    private String readTerm(ParserState state) {
        int start = state.pos;
        while (!state.isEof() && !isDelimiter(state.peek())) {
            state.consume();
        }
        return state.text.substring(start, state.pos);
    }

    private boolean isDelimiter(char c) {
        return Character.isWhitespace(c) || Operator.find(c).isPresent();
    }

    private Query buildBooleanQuery(Query left, Query right, Operator operator) {
        if (left instanceof EmptyQuery) {
            return right;
        } else if (right instanceof EmptyQuery) {
            return left;
        }

        return switch (operator) {
            case OR -> buildCompoundQuery(left, right, Occur.SHOULD, Occur.SHOULD);
            case NOT -> buildCompoundQuery(left, right, Occur.MUST, Occur.MUST_NOT);
        };
    }

    private static CompoundQuery buildCompoundQuery(Query leftQuery, Query rightQuery,
                                                    Occur leftOccur, Occur rightOccur) {
        return new CompoundQuery(List.of(
                new Clause(leftQuery, leftOccur),
                new Clause(rightQuery, rightOccur)
        ));
    }

    private static class ParserState {
        private final String text;
        private int pos;

        ParserState(String text) {
            this.text = text;
            this.pos = 0;
        }

        char peek() {
            return text.charAt(pos);
        }

        char consume() {
            return text.charAt(pos++);
        }

        boolean isEof() {
            return pos >= text.length();
        }

        void consumeWhitespace() {
            while (!isEof() && Character.isWhitespace(peek())) {
                pos++;
            }
        }
    }
}
