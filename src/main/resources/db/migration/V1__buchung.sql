CREATE TABLE buchung (
    id                        UUID         NOT NULL PRIMARY KEY,
    concurrency_version       BIGINT       NOT NULL,
    reservierungsnummer_value VARCHAR(50),
    aufenthaltszeitraum_anreise DATE       NOT NULL,
    aufenthaltszeitraum_abreise DATE       NOT NULL,
    zimmer_id                 VARCHAR(20),
    status                    VARCHAR(50)  NOT NULL,
    CONSTRAINT uk_buchung_reservierungsnummer UNIQUE (reservierungsnummer_value)
);

-- Hauptgast: Entity innerhalb der Buchung, FK auf die Buchung (ohne Java-Feld, von DLC befuellt)
CREATE TABLE gast (
    id                  UUID         NOT NULL PRIMARY KEY,
    concurrency_version BIGINT       NOT NULL,
    buchung_id          UUID         NOT NULL REFERENCES buchung (id),
    name_vorname        VARCHAR(100) NOT NULL,
    name_nachname       VARCHAR(100) NOT NULL,
    email_value         VARCHAR(200),
    telefonnummer_value VARCHAR(30),
    adresse_strasse     VARCHAR(200),
    adresse_postleitzahl VARCHAR(20),
    adresse_ort         VARCHAR(100),
    adresse_land        VARCHAR(100)
);
