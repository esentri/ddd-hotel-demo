package com.esentri.hotel.rezeption.core.domain.verfuegbarkeit;

import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;
import io.domainlifecycles.domain.types.ReadModel;

import java.util.List;

/**
 * @param anzahlFreieZimmer    Zimmer der Kategorie ohne Belegung (Gast oder Wartung) in dieser Nacht
 * @param offeneReservierungen noch nicht eingecheckte Buchungen der Kategorie, deren Aufenthalt diese Nacht umfasst -
 *                             sie brauchen beim CheckIn noch ein Zimmer
 * @param nochVerfuegbar       freie Zimmer abzueglich offener Reservierungen; negativ bei Ueberbuchung
 */
public record VerfuegbarkeitJeKategorie(Zimmerkategorie kategorie,
                                        int anzahlFreieZimmer,
                                        List<Zimmernummer> freieZimmer,
                                        int offeneReservierungen,
                                        int nochVerfuegbar) implements ReadModel {
}
