package com.nilgil.book.core.search.keyword;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeywordSearchedEventHandler {

    private final SearchKeywordRepository searchKeywordCounter;

    @Async
    @EventListener
    public void handle(KeywordSearchedEvent event) {
        searchKeywordCounter.increment(event.parsedQuery());
    }
}
