package com.esentri.rezeption.domaene.buchung;

import com.esentri.rezeption.domain.buchung.Buchung;
import com.esentri.rezeption.domain.buchung.BuchungsId;
import com.esentri.rezeption.domain.buchung.BuchungsStatus;
import com.esentri.rezeption.domain.buchung.Hauptgast;
import com.esentri.rezeption.domain.buchung.HauptgastId;
import com.esentri.rezeption.domain.buchung.Zeitraum;
import com.esentri.rezeption.domain.buchung.ZimmerId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BuchungTest {

    @Test
    void testNeueBuchung() {
        BuchungsId id = new BuchungsId(UUID.randomUUID());
        Hauptgast gast = Hauptgast.builder()
                .id(new HauptgastId(UUID.randomUUID()))
                .nachname("Mustermann")
                .build();
        Zeitraum zeitraum = new Zeitraum(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));

        Buchung buchung = Buchung.neueBuchung(id, gast, zeitraum);

        assertEquals(id, buchung.id());
        assertEquals(BuchungsStatus.RESERVIERT, buchung.getStatus());
        assertTrue(buchung.getZimmerId().isEmpty());
    }

    @Test
    void testCheckInErfolgreich() {
        BuchungsId id = new BuchungsId(UUID.randomUUID());
        Hauptgast gast = Hauptgast.builder()
                .id(new HauptgastId(UUID.randomUUID()))
                .vorname("Max")
                .nachname("Mustermann")
                .geburtsdatum(LocalDate.now().minusYears(20))
                .build();
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(5));
        Buchung buchung = Buchung.neueBuchung(id, gast, zeitraum);

        ZimmerId zimmerId = new ZimmerId(UUID.randomUUID());
        buchung.checkeEin(zimmerId);

        assertEquals(BuchungsStatus.EINGECHECKT, buchung.getStatus());
        assertEquals(Optional.of(zimmerId), buchung.getZimmerId());
    }

    @Test
    void testCheckInZuJung() {
        BuchungsId id = new BuchungsId(UUID.randomUUID());
        Hauptgast gast = Hauptgast.builder()
                .id(new HauptgastId(UUID.randomUUID()))
                .vorname("Max")
                .nachname("Mustermann")
                .geburtsdatum(LocalDate.now().minusYears(15))
                .build();
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(5));
        Buchung buchung = Buchung.neueBuchung(id, gast, zeitraum);

        ZimmerId zimmerId = new ZimmerId(UUID.randomUUID());
        assertThrows(IllegalStateException.class, () -> buchung.checkeEin(zimmerId));
    }

    @Test
    void testStornieren() {
        BuchungsId id = new BuchungsId(UUID.randomUUID());
        Hauptgast gast = Hauptgast.builder()
                .id(new HauptgastId(UUID.randomUUID()))
                .nachname("Mustermann")
                .build();
        Zeitraum zeitraum = new Zeitraum(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));
        Buchung buchung = Buchung.neueBuchung(id, gast, zeitraum);

        buchung.storniere();

        assertEquals(BuchungsStatus.STORNIERT, buchung.getStatus());
    }

    @Test
    void testStornierenNichtErlaubt() {
        BuchungsId id = new BuchungsId(UUID.randomUUID());
        Hauptgast gast = Hauptgast.builder()
                .id(new HauptgastId(UUID.randomUUID()))
                .vorname("Max")
                .nachname("Mustermann")
                .geburtsdatum(LocalDate.now().minusYears(20))
                .build();
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(5));
        Buchung buchung = Buchung.neueBuchung(id, gast, zeitraum);
        buchung.checkeEin(new ZimmerId(UUID.randomUUID()));

        assertThrows(IllegalStateException.class, buchung::storniere);
    }
}
