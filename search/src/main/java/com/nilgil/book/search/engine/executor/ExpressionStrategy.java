package com.nilgil.book.search.engine.executor;

import com.nilgil.book.search.engine.parser.model.Query;

interface ExpressionStrategy {
    String buildExpression(Query query);
}
