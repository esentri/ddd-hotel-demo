package com.esentri.rezeption.domain.model.zimmer.auslastung;

import com.esentri.rezeption.domain.model.zimmer.Zimmerkategorie;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZimmerauslastungTest {

    @Test
    void testReadModelCreation() {
        LocalDate datum = LocalDate.of(2026, 5, 12);
        Zimmerkategorie kategorie = new Zimmerkategorie("Suite");
        Zimmerauslastung auslastung = new Zimmerauslastung(datum, kategorie, 5, 10);

        assertEquals(datum, auslastung.datum());
        assertEquals(kategorie, auslastung.kategorie());
        assertEquals(5, auslastung.belegteZimmer());
        assertEquals(10, auslastung.zimmerGesamt());
    }
}
