-- Belegungen eines Zimmers mit Zeitraum (Entity Belegung im Aggregat Zimmer), bis ausschliesslich
CREATE TABLE belegung (
    id                  UUID         NOT NULL PRIMARY KEY,
    concurrency_version BIGINT       NOT NULL,
    zimmer_id           UUID         NOT NULL REFERENCES zimmer (id),
    art                 VARCHAR(50)  NOT NULL,
    zeitraum_von        DATE         NOT NULL,
    zeitraum_bis        DATE         NOT NULL,
    buchungs_id         UUID,
    wartungsgrund_value VARCHAR(200),
    CONSTRAINT uk_belegung_buchungs_id UNIQUE (buchungs_id)
);

-- bestehende Gast-Belegungen uebernehmen: Zeitraum = Aufenthalt der Buchung
INSERT INTO belegung (id, concurrency_version, zimmer_id, art, zeitraum_von, zeitraum_bis, buchungs_id)
SELECT RANDOM_UUID(), 1, z.id, 'GAST', b.aufenthaltszeitraum_anreise, b.aufenthaltszeitraum_abreise, b.id
FROM zimmer z
JOIN buchung b ON b.id = z.belegung_buchungs_id
WHERE z.belegung_art = 'GAST';

-- bestehende Wartungen hatten kein Ende: als eintaegige Wartung ab heute uebernehmen
INSERT INTO belegung (id, concurrency_version, zimmer_id, art, zeitraum_von, zeitraum_bis, wartungsgrund_value)
SELECT RANDOM_UUID(), 1, z.id, 'WARTUNG', CURRENT_DATE, DATEADD(DAY, 1, CURRENT_DATE), z.belegung_wartungsgrund_value
FROM zimmer z
WHERE z.belegung_art = 'WARTUNG';

ALTER TABLE zimmer DROP CONSTRAINT uk_zimmer_belegung_buchungs_id;
ALTER TABLE zimmer DROP COLUMN belegung_art;
ALTER TABLE zimmer DROP COLUMN belegung_buchungs_id;
ALTER TABLE zimmer DROP COLUMN belegung_wartungsgrund_value;
