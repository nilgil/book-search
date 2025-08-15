package com.nilgil.book.search.keyword;

import com.nilgil.book.search.engine.SearchedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchedEventHandler {

    private final SearchKeywordRepository searchKeywordCounter;

    @Async
    @EventListener
    public void handle(SearchedEvent event) {
        searchKeywordCounter.increment(event.keywords());
    }
}
