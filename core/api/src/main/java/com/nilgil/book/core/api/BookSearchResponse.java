package com.nilgil.book.core.api;

import com.nilgil.book.search.engine.executor.model.BookHit;
import com.nilgil.book.search.engine.executor.model.BookSearchResult;
import com.nilgil.book.search.engine.executor.model.Metadata;
import com.nilgil.book.share.PageResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record BookSearchResponse(
        String searchQuery,
        PageResponse pageResponse,
        List<BookResponse> books,
        SearchMetadata searchMetadata
) {

    public static BookSearchResponse from(String query, BookSearchResult result) {
        return BookSearchResponse.builder()
                .searchQuery(query)
                .pageResponse(result.pageResponse())
                .books(getBookResponses(result))
                .searchMetadata(getSearchMetadata(result))
                .build();
    }

    private static List<BookResponse> getBookResponses(BookSearchResult result) {
        return result.bookHits().stream().map(BookResponse::from).toList();
    }

    private static SearchMetadata getSearchMetadata(BookSearchResult result) {
        Metadata metadata = result.metadata();
        return new SearchMetadata(metadata.executionTime(), metadata.strategy().toString());
    }

    @Builder
    public record BookResponse(
            String id,
            String title,
            String subtitle,
            String image,
            String author,
            String isbn,
            String published
    ) {
        public static BookResponse from(BookHit bookHit) {
            return BookResponse.builder()
                    .id(bookHit.isbn())
                    .title(bookHit.title())
                    .subtitle(bookHit.subtitle())
                    .image(bookHit.image())
                    .author(bookHit.author())
                    .isbn(bookHit.isbn())
                    .published(bookHit.published())
                    .build();
        }
    }

    public record SearchMetadata(long executionTime, String strategy) {
    }
}
