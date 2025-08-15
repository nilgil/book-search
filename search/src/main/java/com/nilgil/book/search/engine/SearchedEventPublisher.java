package com.nilgil.book.search.engine;

import com.nilgil.book.search.engine.parser.model.CompoundQuery;
import com.nilgil.book.search.engine.parser.model.Query;
import com.nilgil.book.search.engine.parser.model.TermQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SearchedEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publish(Query query) {
        Set<String> keywords = extractKeywords(query);
        publisher.publishEvent(new SearchedEvent(keywords));
    }

    public Set<String> extractKeywords(Query query) {
        return switch (query) {
            case TermQuery tq -> {
                if (tq.value() == null || tq.value().isBlank()) {
                    yield Collections.emptySet();
                }
                yield Set.of(tq.value());
            }

            case CompoundQuery cq -> cq.clauses().stream()
                    .flatMap(clause -> extractKeywords(clause.query()).stream())
                    .collect(Collectors.toSet());

            default -> Collections.emptySet();
        };
    }

}
