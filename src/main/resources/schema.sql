CREATE EXTENSION IF NOT EXISTS textsearch_ko;

set default_text_search_config = korean;

CREATE TABLE IF NOT EXISTS book_read_model
(
    isbn13         CHAR(13) PRIMARY KEY,
    title          VARCHAR(300) NOT NULL,
    subtitle       VARCHAR(300),
    description    TEXT,
    image          TEXT,
    author         VARCHAR(200) NOT NULL,
    translator     VARCHAR(200),
    publisher      VARCHAR(200) NOT NULL,
    published_date DATE         NOT NULL,
    page_count     INTEGER      NOT NULL
);

ALTER TABLE IF EXISTS book_read_model
    ADD COLUMN IF NOT EXISTS search_vector tsvector
        GENERATED ALWAYS AS (
            setweight(to_tsvector('korean', coalesce(title, '')), 'A') ||
            setweight(to_tsvector('korean', coalesce(author, '')), 'A') ||
            setweight(to_tsvector('korean', coalesce(description, '')), 'B') ||
            setweight(to_tsvector('korean', coalesce(subtitle, '')), 'B') ||
            setweight(to_tsvector('korean', coalesce(publisher, '')), 'D') ||
            setweight(to_tsvector('korean', coalesce(translator, '')), 'D')
            ) STORED;

CREATE INDEX IF NOT EXISTS idx_book_search_vector
    ON book_read_model
        USING gin (search_vector);
