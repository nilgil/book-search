package com.nilgil.book.core.query;

import java.util.Map;

public record PopularKeywordsResponse(
        Map<String, Long> keywords
) {
}
