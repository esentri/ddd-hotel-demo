package com.esentri.rezeption.domain.buchung;

import com.esentri.rezeption.domain.Zeitraum;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuchungsFactoryTest {

    private static final LocalDate HEUTE = LocalDate.of(2030, 5, 1);

    private final BuchungsFactory factory =
        new BuchungsFactory(Clock.fixed(HEUTE.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC));

    @Test
    void testReserviertEineNeueBuchungMitHauptgast() {
        // Given
        Zeitraum zeitraum = new Zeitraum(HEUTE, HEUTE.plusDays(2));

        // When
        Buchung buchung = factory.reserviere("John", "Doe", LocalDate.of(1990, 1, 1), zeitraum);

        // Then
        assertNotNull(buchung.id());
        assertEquals(BuchungsStatus.RESERVIERT, buchung.getStatus());
        assertEquals(zeitraum, buchung.getBelegungszeitraum());
        assertNotNull(buchung.getHauptGast().id());
        assertEquals("John", buchung.getHauptGast().getVorname());
        assertEquals("Doe", buchung.getHauptGast().getNachname());
    }

    @Test
    void testVergibtJederBuchungEigeneIdentitaeten() {
        // Given
        Zeitraum zeitraum = new Zeitraum(HEUTE, HEUTE.plusDays(2));

        // When
        Buchung erste = factory.reserviere("John", "Doe", null, zeitraum);
        Buchung zweite = factory.reserviere("John", "Doe", null, zeitraum);

        // Then
        assertNotEquals(erste.id(), zweite.id());
        assertNotEquals(erste.getHauptGast().id(), zweite.getHauptGast().id());
    }

    @Test
    void testLehntEineBuchungAbDieInDerVergangenheitBeginnt() {
        // Given
        Zeitraum zeitraum = new Zeitraum(HEUTE.minusDays(1), HEUTE.plusDays(2));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> factory.reserviere("John", "Doe", null, zeitraum));
    }
}
