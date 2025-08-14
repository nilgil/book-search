package com.nilgil.book.query;

import java.util.Map;

public record PopularKeywordsResponse(
        Map<String, Long> keywords
) {
}
