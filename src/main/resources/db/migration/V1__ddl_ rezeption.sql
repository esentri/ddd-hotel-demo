CREATE SCHEMA IF NOT EXISTS rezeption;
SET SCHEMA rezeption;

CREATE TABLE rezeption.zimmer (
    id UUID PRIMARY KEY,
    kategorie VARCHAR(100) NOT NULL,
    concurrency_version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE rezeption.zimmer_belegungen (
    id UUID PRIMARY KEY,
    container_id UUID NOT NULL,
    zeitraum_start DATE NOT NULL,
    zeitraum_ende DATE NOT NULL,
    typ VARCHAR(50) NOT NULL,
    buchungs_id UUID,
    FOREIGN KEY (container_id) REFERENCES zimmer(id)
);

CREATE TABLE rezeption.buchung (
    id UUID PRIMARY KEY,
    belegungszeitraum_start DATE NOT NULL,
    belegungszeitraum_ende DATE NOT NULL,
    zimmer_id UUID,
    status VARCHAR(50) NOT NULL,
    concurrency_version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE rezeption.haupt_gast (
    id UUID PRIMARY KEY,
    buchung_id UUID NOT NULL UNIQUE,
    vorname VARCHAR(255),
    nachname VARCHAR(255),
    geburtsdatum DATE,
    concurrency_version BIGINT NOT NULL DEFAULT 0,
    FOREIGN KEY (buchung_id) REFERENCES buchung(id)
);

CREATE VIEW rezeption.v_zimmerauslastung AS
WITH RECURSIVE kalender(datum, max_datum) AS (
    SELECT MIN(zeitraum_start), MAX(zeitraum_ende)
    FROM rezeption.zimmer_belegungen
    UNION ALL
    SELECT DATEADD('DAY', 1, datum), max_datum
    FROM kalender
    WHERE datum < max_datum
),
kategorien(kategorie) AS (
    SELECT DISTINCT kategorie FROM rezeption.zimmer
),
zimmer_pro_kategorie(kategorie, gesamt) AS (
    SELECT kategorie, COUNT(*) as gesamt
    FROM rezeption.zimmer
    GROUP BY kategorie
),
belegte_zimmer_pro_tag(datum, kategorie, belegt) AS (
    SELECT
        k.datum,
        z.kategorie,
        COUNT(DISTINCT zb.container_id) as belegt
    FROM kalender k
    CROSS JOIN rezeption.zimmer z
    JOIN rezeption.zimmer_belegungen zb ON zb.container_id = z.id
    WHERE k.datum >= zb.zeitraum_start AND k.datum <= zb.zeitraum_ende
    GROUP BY k.datum, z.kategorie
)
SELECT
    k.datum,
    cat.kategorie,
    CAST(COALESCE(b.belegt, 0) AS INTEGER) as anzahl_belegt,
    CAST(COALESCE(zpk.gesamt, 0) AS INTEGER) as anzahl_gesamt
FROM kalender k
CROSS JOIN kategorien cat
LEFT JOIN zimmer_pro_kategorie zpk ON zpk.kategorie = cat.kategorie
LEFT JOIN belegte_zimmer_pro_tag b ON b.datum = k.datum AND b.kategorie = cat.kategorie;
