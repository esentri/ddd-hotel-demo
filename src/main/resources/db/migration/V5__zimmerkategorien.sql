-- Zimmerkategorien (Einzel-/Doppelzimmer) fuer Zimmer und Buchungen.
-- Bestehende Zimmer und Reservierungen hatten keine Kategorie: sie werden als Doppelzimmer uebernommen.
ALTER TABLE zimmer ADD COLUMN kategorie VARCHAR(50) DEFAULT 'DOPPELZIMMER' NOT NULL;
ALTER TABLE zimmer ALTER COLUMN kategorie DROP DEFAULT;

ALTER TABLE buchung ADD COLUMN zimmerkategorie VARCHAR(50) DEFAULT 'DOPPELZIMMER' NOT NULL;
ALTER TABLE buchung ALTER COLUMN zimmerkategorie DROP DEFAULT;
-- eingecheckte Buchungen: Kategorie des zugewiesenen Zimmers
UPDATE buchung b SET zimmerkategorie = (SELECT z.kategorie FROM zimmer z WHERE z.id = b.zimmer_id)
WHERE b.zimmer_id IS NOT NULL;
