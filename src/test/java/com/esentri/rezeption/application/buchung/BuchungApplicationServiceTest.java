package com.esentri.rezeption.application.buchung;

import com.esentri.rezeption.domain.zimmer.ZimmerId;
import com.esentri.rezeption.domain.buchung.AktualisiereGastdaten;
import com.esentri.rezeption.domain.buchung.Buchung;
import com.esentri.rezeption.domain.buchung.BuchungRepository;
import com.esentri.rezeption.domain.buchung.BuchungsId;
import com.esentri.rezeption.domain.buchung.CheckInService;
import com.esentri.rezeption.domain.buchung.CheckeGastAus;
import com.esentri.rezeption.domain.buchung.HauptGast;
import com.esentri.rezeption.domain.buchung.HauptGastId;
import com.esentri.rezeption.domain.Zeitraum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BuchungApplicationServiceTest {

    private BuchungRepository buchungRepository;
    private CheckInService checkInService;
    private BuchungApplicationService underTest;

    @BeforeEach
    void setUp() {
        buchungRepository = mock(BuchungRepository.class);
        checkInService = mock(CheckInService.class);
        underTest = new BuchungApplicationService(buchungRepository, checkInService);
    }

    @Test
    void testAktualisiereGastdatenErfolgreich() {
        // Given
        BuchungsId buchungsId = new BuchungsId(UUID.randomUUID());
        HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1));
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        Buchung buchung = Buchung.neueBuchung(buchungsId, gast, zeitraum);

        when(buchungRepository.findById(buchungsId)).thenReturn(Optional.of(buchung));

        AktualisiereGastdaten command = new AktualisiereGastdaten(
            buchungsId,
            "Jane",
            "Smith",
            LocalDate.of(1995, 5, 5)
        );

        // When
        BuchungsId resultId = underTest.aktualisiereGastdaten(command);

        // Then
        assertEquals(buchungsId, resultId);
        assertEquals("Jane", buchung.getHauptGast().getVorname());
        assertEquals("Smith", buchung.getHauptGast().getNachname());
        assertEquals(LocalDate.of(1995, 5, 5), buchung.getHauptGast().getGeburtsdatum());

        verify(buchungRepository).update(buchung);
    }

    @Test
    void testAktualisiereGastdatenBuchungNichtGefunden() {
        // Given
        BuchungsId buchungsId = new BuchungsId(UUID.randomUUID());
        when(buchungRepository.findById(buchungsId)).thenReturn(Optional.empty());

        AktualisiereGastdaten command = new AktualisiereGastdaten(
            buchungsId,
            "Jane",
            "Smith",
            LocalDate.of(1995, 5, 5)
        );

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> underTest.aktualisiereGastdaten(command));
        verify(buchungRepository, never()).update(any());
    }

    @Disabled("Benoetigt DomainEvents Initialisierung")
    @Test
    void testCheckeGastAusErfolgreich() {
        // Given
        BuchungsId buchungsId = new BuchungsId(UUID.randomUUID());
        HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1));
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        Buchung buchung = Buchung.neueBuchung(buchungsId, gast, zeitraum);
        buchung.checkeEin(new ZimmerId(UUID.randomUUID()));

        when(buchungRepository.findById(buchungsId)).thenReturn(Optional.of(buchung));

        CheckeGastAus command = new CheckeGastAus(buchungsId);

        // When
        BuchungsId resultId = underTest.checkeGastAus(command);

        // Then
        assertEquals(buchungsId, resultId);
        verify(buchungRepository).update(buchung);
    }
}
