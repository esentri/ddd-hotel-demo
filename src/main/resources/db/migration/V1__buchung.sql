CREATE TABLE buchung (
    id                          CHAR(36)    NOT NULL PRIMARY KEY,
    concurrency_version         BIGINT      NOT NULL,
    buchungszeitraum_anreise    DATE        NOT NULL,
    buchungszeitraum_abreise    DATE        NOT NULL,
    gewuenschte_zimmerkategorie VARCHAR(50) NOT NULL,
    status                      VARCHAR(50) NOT NULL
);

CREATE TABLE gast (
    id                  CHAR(36)     NOT NULL PRIMARY KEY,
    concurrency_version BIGINT       NOT NULL,
    buchung_id          CHAR(36)     NOT NULL UNIQUE REFERENCES buchung (id),
    vorname             VARCHAR(100),
    nachname            VARCHAR(100) NOT NULL
);
