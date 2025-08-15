package com.nilgil.book.core.query;

import com.nilgil.book.core.search.executor.model.BookHit;
import com.nilgil.book.core.search.executor.model.BookSearchResult;
import com.nilgil.book.core.search.executor.model.Metadata;
import com.nilgil.book.core.share.PageInfo;
import lombok.Builder;

import java.util.List;

@Builder
public record BookSearchResponse(
        String searchQuery,
        PageInfo pageInfo,
        List<BookResponse> books,
        SearchMetadata searchMetadata
) {

    public static BookSearchResponse from(String query, BookSearchResult result) {
        return BookSearchResponse.builder()
                .searchQuery(query)
                .pageInfo(result.pageInfo())
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
