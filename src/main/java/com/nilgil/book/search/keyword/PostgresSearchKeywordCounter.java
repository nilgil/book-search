package com.nilgil.book.search.keyword;

import com.nilgil.book.search.parser.model.Clause;
import com.nilgil.book.search.parser.model.CompoundQuery;
import com.nilgil.book.search.parser.model.Query;
import com.nilgil.book.search.parser.model.TermQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostgresSearchKeywordCounter implements SearchKeywordCounter {

    private final PostgresSearchKeywordRepository repository;

    @Override
    public void increment(Query query) {
        switch (query) {
            case TermQuery tq -> repository.incrementCount(tq.value());
            case CompoundQuery cq -> cq.clauses().forEach(this::increment);
            default -> {
            }
        }
    }

    private void increment(Clause clause) {
        increment(clause.query());
    }
}
