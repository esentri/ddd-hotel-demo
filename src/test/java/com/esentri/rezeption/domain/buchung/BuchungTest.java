package com.esentri.rezeption.domain.buchung;

import com.esentri.rezeption.domain.Zeitraum;
import com.esentri.rezeption.domain.zimmer.ZimmerId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BuchungTest {

    @Test
    void testNeueBuchung() {
        BuchungsId id = new BuchungsId(UUID.randomUUID());
        HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1), 0);
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));

        Buchung buchung = Buchung.neueBuchung(id, gast, zeitraum);

        assertEquals(id, buchung.id());
        assertEquals(BuchungsStatus.RESERVIERT, buchung.getStatus());
        assertTrue(buchung.getZimmerId().isEmpty());
    }

    @Test
    void testCheckeEin() {
        BuchungsId id = new BuchungsId(UUID.randomUUID());
        HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1), 0);
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        Buchung buchung = Buchung.neueBuchung(id, gast, zeitraum);
        ZimmerId zimmerId = new ZimmerId(UUID.randomUUID());

        buchung.checkeEin(zimmerId);

        assertEquals(BuchungsStatus.EINGECHECKT, buchung.getStatus());
        assertEquals(zimmerId, buchung.getZimmerId().get());
    }

    @Test
    void testCheckeEinZuJung() {
        BuchungsId id = new BuchungsId(UUID.randomUUID());
        // Gast ist 15 Jahre alt zum Check-in
        HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "Young", "Doe", LocalDate.now().minusYears(15), 0);
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        Buchung buchung = Buchung.neueBuchung(id, gast, zeitraum);
        ZimmerId zimmerId = new ZimmerId(UUID.randomUUID());

        assertThrows(IllegalStateException.class, () -> buchung.checkeEin(zimmerId));
    }

    @Test
    void testStorniere() {
        BuchungsId id = new BuchungsId(UUID.randomUUID());
        HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1), 0);
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        Buchung buchung = Buchung.neueBuchung(id, gast, zeitraum);

        buchung.storniere();

        assertEquals(BuchungsStatus.STORNIERT, buchung.getStatus());
    }

    @Test
    void testStorniereFehlgeschlagen() {
        BuchungsId id = new BuchungsId(UUID.randomUUID());
        HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1), 0);
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        Buchung buchung = Buchung.neueBuchung(id, gast, zeitraum);
        buchung.checkeEin(new ZimmerId(UUID.randomUUID()));

        assertThrows(IllegalStateException.class, buchung::storniere);
    }

    @Test
    void testCheckeAus() {
        BuchungsId id = new BuchungsId(UUID.randomUUID());
        HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1), 0);
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        Buchung buchung = Buchung.neueBuchung(id, gast, zeitraum);
        buchung.checkeEin(new ZimmerId(UUID.randomUUID()));

        buchung.checkeAus();

        assertEquals(BuchungsStatus.AUSGECHECKT, buchung.getStatus());
    }

    @Test
    void testCheckeAusNichtEingecheckt() {
        BuchungsId id = new BuchungsId(UUID.randomUUID());
        HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1), 0);
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        Buchung buchung = Buchung.neueBuchung(id, gast, zeitraum);

        assertThrows(IllegalStateException.class, buchung::checkeAus);
    }

}
