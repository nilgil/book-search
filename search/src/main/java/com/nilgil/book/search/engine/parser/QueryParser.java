package com.nilgil.book.search.engine.parser;

import com.nilgil.book.search.engine.parser.model.Query;

public interface QueryParser {
    Query parse(String rawQuery);
}
