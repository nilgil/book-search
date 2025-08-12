package com.nilgil.book.search.parser;

import com.nilgil.book.search.parser.model.Query;

public interface QueryParser {
    Query parse(String rawQuery);
}
