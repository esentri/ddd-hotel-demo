package com.esentri.rezeption.domain.zimmer;

import com.esentri.rezeption.domain.buchung.BuchungsId;
import com.esentri.rezeption.domain.Zeitraum;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ZimmerTest {

    @Test
    void testBelegung() {
        Zimmer zimmer = new Zimmer(new ZimmerId(UUID.randomUUID()), Zimmerkategorie.EINZELZIMMER);
        BuchungsId buchungsId = new BuchungsId(UUID.randomUUID());
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));

        zimmer.belegeFuer(buchungsId, zeitraum);

        assertEquals(1, zimmer.getBelegungen().size());
        assertEquals(BelegungsTyp.CHECKIN, zimmer.getBelegungen().get(0).typ());
        assertEquals(buchungsId, zimmer.getBelegungen().get(0).buchungsId());
    }

    @Test
    void testBelegungUeberlappend() {
        Zimmer zimmer = new Zimmer(new ZimmerId(UUID.randomUUID()), Zimmerkategorie.EINZELZIMMER);
        BuchungsId buchungsId1 = new BuchungsId(UUID.randomUUID());
        Zeitraum zeitraum1 = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        zimmer.belegeFuer(buchungsId1, zeitraum1);

        BuchungsId buchungsId2 = new BuchungsId(UUID.randomUUID());
        Zeitraum zeitraum2 = new Zeitraum(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));

        assertThrows(IllegalStateException.class, () -> zimmer.belegeFuer(buchungsId2, zeitraum2));
    }

    @Test
    void testPlaneWartung() {
        Zimmer zimmer = new Zimmer(new ZimmerId(UUID.randomUUID()), Zimmerkategorie.EINZELZIMMER);
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(1));

        zimmer.planeWartung(zeitraum);

        assertEquals(1, zimmer.getBelegungen().size());
        assertEquals(BelegungsTyp.WARTUNG, zimmer.getBelegungen().get(0).typ());
        assertTrue(zimmer.getBelegungen().get(0).buchungsId() == null);
    }

    @Test
    void testCheckOut() {
        Zimmer zimmer = new Zimmer(new ZimmerId(UUID.randomUUID()), Zimmerkategorie.EINZELZIMMER);
        BuchungsId buchungsId = new BuchungsId(UUID.randomUUID());
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        zimmer.belegeFuer(buchungsId, zeitraum);

        zimmer.gibFrei(buchungsId);

        assertTrue(zimmer.getBelegungen().isEmpty());
    }

    @Test
    void testIstVerfuegbarFuer() {
        Zimmer zimmer = new Zimmer(new ZimmerId(UUID.randomUUID()), Zimmerkategorie.EINZELZIMMER);
        Zeitraum zeitraum1 = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        zimmer.belegeFuer(new BuchungsId(UUID.randomUUID()), zeitraum1);

        Zeitraum zeitraum2 = new Zeitraum(LocalDate.now().plusDays(2), LocalDate.now().plusDays(4));
        Zeitraum zeitraumUeberlappend = new Zeitraum(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));

        assertTrue(zimmer.istVerfuegbarFuer(zeitraum2), "Zimmer sollte fuer den Zeitraum nach der Belegung verfuegbar sein.");
        assertFalse(zimmer.istVerfuegbarFuer(zeitraumUeberlappend), "Zimmer sollte fuer einen ueberlappenden Zeitraum nicht verfuegbar sein.");
    }
}
