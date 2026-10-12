package com.esentri.hotel.rezeption.core.domain.verfuegbarkeit;

import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import io.domainlifecycles.domain.types.ReadModel;

import java.time.LocalDate;
import java.util.List;

/**
 * Freie Zimmer eines Hotels Tag fuer Tag. Ein Zimmer ist an einem Tag frei, wenn es fuer die Nacht ab diesem Tag
 * durch nichts belegt ist (weder Gast noch Wartung). Offene Reservierungen haben noch kein Zimmer und werden je Tag
 * und Zimmerkategorie gegen die freien Zimmer gerechnet.
 */
public record FreieZimmerUebersicht(HotelId hotelId,
                                    LocalDate von,
                                    LocalDate bis,
                                    int anzahlZimmer,
                                    List<FreieZimmerAmTag> tage) implements ReadModel {
}
