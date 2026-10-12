-- Mehrere Hotels: jede Buchung gehoert zu genau einem Hotel
ALTER TABLE buchung ADD COLUMN hotel_id UUID NOT NULL;

-- Zimmer sind global eindeutig (UUID) statt ueber eine fachliche Kennung
ALTER TABLE buchung ALTER COLUMN zimmer_id SET DATA TYPE UUID;

-- Zimmernummer nur innerhalb eines Hotels eindeutig; ein Zimmer ist frei oder durch genau eine Buchung belegt.
-- Bewusst keine Fremdschluessel zwischen den Aggregaten Buchung und Zimmer (zyklische Referenz).
CREATE TABLE zimmer (
    id                  UUID        NOT NULL PRIMARY KEY,
    concurrency_version BIGINT      NOT NULL,
    hotel_id            UUID        NOT NULL,
    zimmernummer_value  VARCHAR(10) NOT NULL,
    belegt_durch        UUID,
    CONSTRAINT uk_zimmer_hotel_zimmernummer UNIQUE (hotel_id, zimmernummer_value),
    CONSTRAINT uk_zimmer_belegt_durch UNIQUE (belegt_durch)
);
