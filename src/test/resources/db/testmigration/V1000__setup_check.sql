CREATE TABLE setup_check (
    id                  UUID         NOT NULL PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    concurrency_version BIGINT       NOT NULL
);
