CREATE TABLE IF NOT EXISTS book
(
    isbn13         CHAR(13) PRIMARY KEY,
    title          VARCHAR(300) NOT NULL,
    subtitle       VARCHAR(300),
    description    TEXT,
    image          TEXT,
    author         VARCHAR(200),
    translator     VARCHAR(200),
    publisher      VARCHAR(200),
    published_date DATE,
    page_count     INTEGER
);
