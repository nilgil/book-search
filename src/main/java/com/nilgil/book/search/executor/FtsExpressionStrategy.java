package com.nilgil.book.search.executor;

import com.nilgil.book.search.parser.model.Query;

interface FtsExpressionStrategy {
    String buildExpression(Query query);
}
