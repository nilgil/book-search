package com.nilgil.book.search.engine.planner;

import com.nilgil.book.search.engine.parser.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SimpleQueryPlanner implements QueryPlanner {

    @Override
    public SearchStrategy plan(Query query) {
        return switch (query) {
            case TermQuery tq -> SearchStrategy.SINGLE_TERM;
            case CompoundQuery cq -> determineStrategyForCompound(cq);
            default -> SearchStrategy.NONE;
        };
    }

    private SearchStrategy determineStrategyForCompound(CompoundQuery compoundQuery) {
        List<Clause> clauses = compoundQuery.clauses();

        return switch (clauses.size()) {
            case 1 -> determineForSingleClause(clauses.getFirst());
            case 2 -> determineForTwoClauses(clauses.getFirst(), clauses.getLast());
            default -> SearchStrategy.NONE;
        };
    }

    private SearchStrategy determineForSingleClause(Clause clause) {
        return switch (clause.query()) {
            case TermQuery tq -> SearchStrategy.SINGLE_TERM;
            case CompoundQuery cq -> determineStrategyForCompound(cq);
            default -> SearchStrategy.NONE;
        };
    }

    private SearchStrategy determineForTwoClauses(Clause left, Clause right) {
        if (isOrOperation(left, right)) {
            return SearchStrategy.OR_OPERATION;
        }
        if (isMustAndNotOperation(left, right)) {
            return SearchStrategy.MUST_AND_NOT_OPERATION;
        }
        return SearchStrategy.NONE;
    }

    private boolean isOrOperation(Clause left, Clause right) {
        return left.occur() == Occur.SHOULD && right.occur() == Occur.SHOULD;
    }

    private boolean isMustAndNotOperation(Clause left, Clause right) {
        return left.occur() == Occur.MUST && right.occur() == Occur.MUST_NOT;
    }
}
