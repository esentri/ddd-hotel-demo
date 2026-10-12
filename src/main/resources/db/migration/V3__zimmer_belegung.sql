-- Zimmer koennen durch einen Gast oder durch Wartung belegt sein (eingebettetes Wertobjekt Belegung)
ALTER TABLE zimmer ADD COLUMN belegung_art VARCHAR(50);
ALTER TABLE zimmer ADD COLUMN belegung_buchungs_id UUID;
ALTER TABLE zimmer ADD COLUMN belegung_wartungsgrund_value VARCHAR(200);

-- bestehende Gast-Belegungen uebernehmen
UPDATE zimmer SET belegung_art = 'GAST', belegung_buchungs_id = belegt_durch WHERE belegt_durch IS NOT NULL;

ALTER TABLE zimmer DROP CONSTRAINT uk_zimmer_belegt_durch;
ALTER TABLE zimmer DROP COLUMN belegt_durch;

-- eine Buchung belegt hoechstens ein Zimmer
ALTER TABLE zimmer ADD CONSTRAINT uk_zimmer_belegung_buchungs_id UNIQUE (belegung_buchungs_id);
