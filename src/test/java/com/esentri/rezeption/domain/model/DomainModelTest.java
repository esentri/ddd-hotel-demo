package com.esentri.rezeption.domain.model;

import com.esentri.rezeption.domain.model.buchung.Buchung;
import com.esentri.rezeption.domain.model.buchung.BuchungId;
import com.esentri.rezeption.domain.model.buchung.Gast;
import com.esentri.rezeption.domain.model.buchung.GastId;
import com.esentri.rezeption.domain.model.zimmer.Zimmer;
import com.esentri.rezeption.domain.model.zimmer.ZimmerId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DomainModelTest {

    @Test
    void testBuchungCheckInGastZuJung() {
        Gast gast = new Gast(new GastId(1L), "Junger", "Gast", LocalDate.now().minusYears(15));
        Buchung buchung = new Buchung(new BuchungId(1L), gast, new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2)));
        
        assertThrows(RuntimeException.class, () -> buchung.checkIn(new ZimmerId(101L)));
    }

    @Test
    void testBuchungCheckInErfolgreich() {
        Gast gast = new Gast(new GastId(1L), "Alter", "Gast", LocalDate.now().minusYears(20));
        Buchung buchung = new Buchung(new BuchungId(1L), gast, new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2)));
        
        buchung.checkIn(new ZimmerId(101L));
        assertEquals(Buchung.Status.EINGECHECKT, buchung.getStatus());
    }

    @Test
    void testZimmerDoppelbelegung() {
        Zimmer zimmer = new Zimmer(new ZimmerId(101L), "101");
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        
        zimmer.fügeBelegungHinzu(zeitraum, new BuchungId(1L));
        
        assertThrows(RuntimeException.class, () -> zimmer.fügeBelegungHinzu(zeitraum, new BuchungId(2L)));
    }

    @Test
    void testStornoNachCheckInNichtMöglich() {
        Gast gast = new Gast(new GastId(1L), "Alter", "Gast", LocalDate.now().minusYears(20));
        Buchung buchung = new Buchung(new BuchungId(1L), gast, new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2)));
        
        buchung.checkIn(new ZimmerId(101L));
        
        assertThrows(RuntimeException.class, buchung::storniere);
    }
}
