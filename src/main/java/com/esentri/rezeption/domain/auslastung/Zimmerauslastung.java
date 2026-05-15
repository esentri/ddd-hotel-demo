package com.esentri.rezeption.domain.auslastung;

import com.esentri.rezeption.domain.zimmer.Zimmerkategorie;
import io.domainlifecycles.domain.types.ReadModel;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Read Model fuer die Zimmerauslastung an einem bestimmten Tag fuer eine Kategorie.
 *
 * @param datum             Der Tag, auf den sich die Auslastung bezieht.
 * @param zimmerkategorie   Die Kategorie des Zimmers.
 * @param anzahlBelegt      Wie viele Zimmer dieser Kategorie sind an diesem Tag belegt.
 * @param anzahlGesamt      Wie viele Zimmer dieser Kategorie hat das Hotel insgesamt.
 */
public record Zimmerauslastung(
    LocalDate datum,
    Zimmerkategorie zimmerkategorie,
    int anzahlBelegt,
    int anzahlGesamt
) implements ReadModel {

    public Zimmerauslastung {
        Objects.requireNonNull(datum, "Datum darf nicht null sein");
        Objects.requireNonNull(zimmerkategorie, "Zimmerkategorie darf nicht null sein");
        
        if (anzahlBelegt < 0) {
            throw new IllegalArgumentException("Anzahl belegter Zimmer darf nicht negativ sein");
        }
        if (anzahlGesamt < 0) {
            throw new IllegalArgumentException("Anzahl gesamter Zimmer darf nicht negativ sein");
        }
        if (anzahlBelegt > anzahlGesamt) {
            throw new IllegalArgumentException("Anzahl belegter Zimmer (%d) darf nicht groesser als die Gesamtanzahl (%d) sein"
                .formatted(anzahlBelegt, anzahlGesamt));
        }
    }

    /**
     * Berechnet die Auslastung in Prozent.
     *
     * @return Prozentwert der Auslastung (0.0 bis 100.0)
     */
    public double auslastungProzent() {
        if (anzahlGesamt == 0) {
            return 0.0;
        }
        return (double) anzahlBelegt / anzahlGesamt * 100.0;
    }
}
