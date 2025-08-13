package com.nilgil.book.search.planner;

import com.nilgil.book.search.parser.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleQueryPlannerTest {

    private final QueryPlanner planner = new SimpleQueryPlanner();

    @Test
    @DisplayName("하나의 용어로 구성된 쿼리는 SINGLE_TERM 전략이 사용된다")
    void shouldPlanTermQuery() {
        Query query = new TermQuery("test");
        PlannedQuery planned = planner.plan(query);

        assertThat(planned.strategy()).isEqualTo(SearchStrategy.SINGLE_TERM);
        assertThat(planned.query()).isEqualTo(query);
    }

    @Test
    @DisplayName("두 쿼리 절이 모두 SHOULD 조건이면 OR_OPERATION 전략이 사용된다")
    void shouldPlanOrOperation() {
        Query query = new CompoundQuery(List.of(
                new Clause(new TermQuery("term1"), Occur.SHOULD),
                new Clause(new TermQuery("term2"), Occur.SHOULD)
        ));
        PlannedQuery planned = planner.plan(query);

        assertThat(planned.strategy()).isEqualTo(SearchStrategy.OR_OPERATION);
        assertThat(planned.query()).isEqualTo(query);
    }

    @Test
    @DisplayName("두 용어가 MUST, MUST_NOT로 구성되면 MUST_AND_NOT_OPERATION 전략이 사용된다")
    void shouldPlanMustAndNotOperation() {
        Query query = new CompoundQuery(List.of(
                new Clause(new TermQuery("must"), Occur.MUST),
                new Clause(new TermQuery("not"), Occur.MUST_NOT)
        ));
        PlannedQuery planned = planner.plan(query);

        assertThat(planned.strategy()).isEqualTo(SearchStrategy.MUST_AND_NOT_OPERATION);
        assertThat(planned.query()).isEqualTo(query);
    }

    @Test
    @DisplayName("유효하지 않은 복합 쿼리에 대해 NONE 전략을 반환 한다")
    void shouldReturnNoneStrategyForInvalidQuery() {
        Query query = new CompoundQuery(List.of(
                new Clause(new TermQuery("term1"), Occur.MUST),
                new Clause(new TermQuery("term2"), Occur.MUST),
                new Clause(new TermQuery("term3"), Occur.MUST)
        ));
        PlannedQuery planned = planner.plan(query);

        assertThat(planned.strategy()).isEqualTo(SearchStrategy.NONE);
        assertThat(planned.query()).isEqualTo(query);
    }
}