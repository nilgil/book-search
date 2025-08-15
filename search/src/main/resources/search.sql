CREATE EXTENSION IF NOT EXISTS textsearch_ko;

set default_text_search_config = korean;

ALTER TABLE IF EXISTS book_read_model
    ADD COLUMN IF NOT EXISTS search_vector tsvector
        GENERATED ALWAYS AS (
            setweight(to_tsvector('korean', coalesce(title, '')), 'A') ||
            setweight(to_tsvector('korean', coalesce(author, '')), 'B') ||
            setweight(to_tsvector('korean', coalesce(subtitle, '')), 'B') ||
            setweight(to_tsvector('korean', coalesce(description, '')), 'C') ||
            setweight(to_tsvector('korean', coalesce(publisher, '')), 'D') ||
            setweight(to_tsvector('korean', coalesce(translator, '')), 'D')
            ) STORED;

CREATE INDEX IF NOT EXISTS idx_book_search_vector
    ON book_read_model
        USING gin (search_vector);

CREATE TABLE IF NOT EXISTS search_keywords
(
    keyword          TEXT PRIMARY KEY,
    search_count     BIGINT    NOT NULL DEFAULT 0,
    last_searched_at TIMESTAMP NOT NULL DEFAULT now()
);
