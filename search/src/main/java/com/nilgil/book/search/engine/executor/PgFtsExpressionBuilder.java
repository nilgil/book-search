package com.nilgil.book.search.engine.executor;

import com.nilgil.book.search.engine.parser.model.Clause;
import com.nilgil.book.search.engine.parser.model.CompoundQuery;
import com.nilgil.book.search.engine.parser.model.Query;
import com.nilgil.book.search.engine.parser.model.TermQuery;
import com.nilgil.book.search.engine.planner.SearchStrategy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PgFtsExpressionBuilder {

    private final Map<SearchStrategy, ExpressionStrategy> strategies = new EnumMap<>(SearchStrategy.class);

    @PostConstruct
    private void initStrategies() {
        strategies.put(SearchStrategy.SINGLE_TERM, query -> {
            if (query instanceof TermQuery(String value)) {
                return value;
            }
            return "";
        });

        strategies.put(SearchStrategy.OR_OPERATION, createCompoundStrategy(" | "));
        strategies.put(SearchStrategy.MUST_AND_NOT_OPERATION, createCompoundStrategy(" & !"));
    }

    public String build(Query query, SearchStrategy strategy) {
        ExpressionStrategy expressionStrategy = strategies.get(strategy);
        if (expressionStrategy == null) {
            return "";
        }
        return expressionStrategy.buildExpression(query);
    }

    private ExpressionStrategy createCompoundStrategy(String operator) {
        return query -> {
            if (query instanceof CompoundQuery c) {
                Pair<String, String> termPair = getTermPair(c);
                return termPair.getFirst() + operator + termPair.getSecond();
            }
            return "";
        };
    }

    private static Pair<String, String> getTermPair(CompoundQuery compoundQuery) {
        List<Clause> clauses = compoundQuery.clauses();

        Clause left = clauses.getFirst();
        Clause right = clauses.getLast();

        TermQuery leftTerm = (TermQuery) left.query();
        TermQuery rightTerm = (TermQuery) right.query();

        return Pair.of(leftTerm.value(), rightTerm.value());
    }
}
