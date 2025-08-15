package com.nilgil.book.core.search.executor;

import com.nilgil.book.core.search.parser.model.Clause;
import com.nilgil.book.core.search.parser.model.CompoundQuery;
import com.nilgil.book.core.search.parser.model.TermQuery;
import com.nilgil.book.core.search.planner.PlannedQuery;
import com.nilgil.book.core.search.planner.SearchStrategy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PostgresFtsExpressionBuilder {

    private final Map<SearchStrategy, FtsExpressionStrategy> strategies = new EnumMap<>(SearchStrategy.class);

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

    public String build(PlannedQuery plannedQuery) {
        FtsExpressionStrategy strategy = strategies.get(plannedQuery.strategy());
        if (strategy == null) {
            return "";
        }
        return strategy.buildExpression(plannedQuery.query());
    }

    private FtsExpressionStrategy createCompoundStrategy(String operator) {
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
