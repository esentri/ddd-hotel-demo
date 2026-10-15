package com.esentri.hotel.rezeption.core.domain.zimmer;

import io.domainlifecycles.domain.types.ReadModel;

/**
 * Ein bezugsfertiges Zimmer in der Uebersicht fuer die Zimmerauswahl beim Check-in. {@code upgrade} ist gesetzt,
 * wenn das Zimmer einer hoeheren als der gewuenschten Zimmerkategorie angehoert.
 */
public record VerfuegbaresZimmer(
    Zimmer.ZimmerId zimmerId,
    Zimmernummer zimmernummer,
    Zimmerkategorie zimmerkategorie,
    boolean upgrade,
    int etage,
    Blickrichtung blickrichtung,
    boolean balkon,
    boolean barrierefrei,
    Bettart bettart,
    boolean raucherzimmer
) implements ReadModel {
}
