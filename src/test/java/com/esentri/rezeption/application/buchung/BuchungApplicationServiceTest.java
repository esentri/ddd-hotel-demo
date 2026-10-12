package com.esentri.rezeption.application.buchung;

import com.esentri.rezeption.domain.zimmer.ZimmerId;
import com.esentri.rezeption.domain.buchung.AktualisiereGastdaten;
import com.esentri.rezeption.domain.buchung.Buchung;
import com.esentri.rezeption.domain.buchung.BuchungRepository;
import com.esentri.rezeption.domain.buchung.BuchungsFactory;
import com.esentri.rezeption.domain.buchung.BuchungsId;
import com.esentri.rezeption.domain.buchung.BuchungsStatus;
import com.esentri.rezeption.domain.buchung.CheckInService;
import com.esentri.rezeption.domain.buchung.CheckeGastAus;
import com.esentri.rezeption.domain.buchung.HauptGast;
import com.esentri.rezeption.domain.buchung.HauptGastId;
import com.esentri.rezeption.domain.buchung.LegeBuchungAn;
import com.esentri.rezeption.domain.Zeitraum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class BuchungApplicationServiceTest {

    private BuchungRepository buchungRepository;
    private CheckInService checkInService;
    private BuchungsFactory buchungsFactory;
    private BuchungApplicationService underTest;

    @BeforeEach
    void setUp() {
        buchungRepository = mock(BuchungRepository.class);
        checkInService = mock(CheckInService.class);
        buchungsFactory = new BuchungsFactory();
        underTest = new BuchungApplicationService(buchungRepository, checkInService, buchungsFactory);
    }

    @Test
    void testLegeBuchungAnErfolgreich() {
        // Given
        Zeitraum zeitraum = new Zeitraum(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        LegeBuchungAn command = new LegeBuchungAn("John", "Doe", LocalDate.of(1990, 1, 1), zeitraum);

        // When
        BuchungsId resultId = underTest.legeBuchungAn(command);

        // Then: die von der Factory reservierte Buchung wird gespeichert
        verify(buchungRepository).insert(argThat(buchung ->
            buchung.id().equals(resultId)
                && buchung.getStatus() == BuchungsStatus.RESERVIERT
                && buchung.getBelegungszeitraum().equals(zeitraum)
                && buchung.getHauptGast().getNachname().equals("Doe")));
    }

    @Test
    void testAktualisiereGastdatenErfolgreich() {
        // Given
        BuchungsId buchungsId = new BuchungsId(UUID.randomUUID());
        HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1), 0);
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

    @Test
    void testCheckeGastAusErfolgreich() {
        // Given
        BuchungsId buchungsId = new BuchungsId(UUID.randomUUID());
        HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1), 0);
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
