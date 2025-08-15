package com.nilgil.book.core.search.parser;

import com.nilgil.book.core.search.parser.model.Query;

public interface QueryParser {
    Query parse(String rawQuery);
}
