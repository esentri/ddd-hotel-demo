CREATE TABLE zimmer (
    id                  CHAR(36)    NOT NULL PRIMARY KEY,
    concurrency_version BIGINT      NOT NULL,
    zimmernummer_value  VARCHAR(10) NOT NULL UNIQUE,
    zimmerkategorie     VARCHAR(50) NOT NULL,
    etage               INT         NOT NULL,
    blickrichtung       VARCHAR(50) NOT NULL,
    balkon              BOOLEAN     NOT NULL,
    barrierefrei        BOOLEAN     NOT NULL,
    bettart             VARCHAR(50) NOT NULL,
    raucherzimmer       BOOLEAN     NOT NULL,
    status              VARCHAR(50) NOT NULL,
    belegt_durch        CHAR(36)
);

ALTER TABLE buchung ADD COLUMN zugewiesenes_zimmer CHAR(36);
