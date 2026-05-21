package com.esentri.rezeption.domain.zimmer;

import com.esentri.rezeption.domain.buchung.BuchungsId;
import com.esentri.rezeption.domain.Zeitraum;
import io.domainlifecycles.domain.types.ValueObject;

import java.util.Objects;

/**
 * Repraesentiert eine Belegung eines Zimmers fuer einen bestimmten Zeitraum.
 */
public record Belegung(
    Zeitraum zeitraum,
    BelegungsTyp typ,
    BuchungsId buchungsId
) implements ValueObject {

    public Belegung {
        Objects.requireNonNull(zeitraum, "Zeitraum darf nicht null sein");
        Objects.requireNonNull(typ, "BelegungsTyp darf nicht null sein");


        if (typ == BelegungsTyp.CHECKIN && buchungsId == null) {
            throw new IllegalArgumentException("Fuer einen Check-in muss eine BuchungsId angegeben werden.");
        }
    }

}
