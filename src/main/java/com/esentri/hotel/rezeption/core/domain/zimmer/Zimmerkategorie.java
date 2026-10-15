package com.esentri.hotel.rezeption.core.domain.zimmer;

import java.util.Arrays;
import java.util.List;

/**
 * Kategorie eines Zimmers, aufsteigend geordnet: ein Zimmer einer hoeheren Kategorie ist ein Upgrade.
 */
public enum Zimmerkategorie {
    EINZELZIMMER,
    DOPPELZIMMER,
    SUITE;

    public boolean istGleichOderHoeherAls(Zimmerkategorie andere) {
        return compareTo(andere) >= 0;
    }

    public List<Zimmerkategorie> gleichOderHoeher() {
        return Arrays.stream(values()).filter(k -> k.istGleichOderHoeherAls(this)).toList();
    }
}
