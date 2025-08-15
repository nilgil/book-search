package com.nilgil.book.core.search.executor;

import com.nilgil.book.core.search.parser.model.Query;

interface FtsExpressionStrategy {
    String buildExpression(Query query);
}
