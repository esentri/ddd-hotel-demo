package com.esentri.rezeption.domain.zimmer;

import com.esentri.rezeption.domain.Zeitraum;
import com.esentri.rezeption.domain.buchung.BuchungsId;
import io.domainlifecycles.domain.types.ValueObject;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Repraesentiert eine Belegung eines Zimmers fuer einen bestimmten Zeitraum.
 */
@Builder
public record Belegung(
    @NotNull(message = "Zeitraum darf nicht null sein") Zeitraum zeitraum,
    @NotNull(message = "BelegungsTyp darf nicht null sein") BelegungsTyp typ,
    BuchungsId buchungsId
) implements ValueObject {

    public Belegung {
        if (typ == BelegungsTyp.CHECKIN && buchungsId == null) {
            throw new IllegalArgumentException("Fuer einen Check-in muss eine BuchungsId angegeben werden.");
        }
    }

}
