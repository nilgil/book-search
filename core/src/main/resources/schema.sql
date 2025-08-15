
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

CREATE TABLE IF NOT EXISTS search_keywords
(
    keyword          TEXT PRIMARY KEY,
    search_count     BIGINT    NOT NULL DEFAULT 0,
    last_searched_at TIMESTAMP NOT NULL DEFAULT now()
);
