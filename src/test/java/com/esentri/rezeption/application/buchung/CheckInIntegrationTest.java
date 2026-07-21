package com.esentri.rezeption.application.buchung;

import com.esentri.rezeption.domain.Zeitraum;
import com.esentri.rezeption.domain.zimmer.ZimmerId;
import com.esentri.rezeption.domain.buchung.*;
import com.esentri.rezeption.domain.zimmer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CheckInIntegrationTest {

    private BuchungRepository buchungRepository;
    private ZimmerRepository zimmerRepository;
    private CheckInService checkInService;
    private BuchungApplicationService buchungApplicationService;

    @BeforeEach
    void setUp() {
        buchungRepository = mock(BuchungRepository.class);
        zimmerRepository = mock(ZimmerRepository.class);
        checkInService = new CheckInService(buchungRepository, zimmerRepository);
        buchungApplicationService = new BuchungApplicationService(buchungRepository, checkInService);
    }

    @Test
    void testErfolgreicherCheckIn() {
        // Arrange
        BuchungsId buchungsId = new BuchungsId(UUID.randomUUID());
        ZimmerId zimmerId = new ZimmerId(UUID.randomUUID());

        HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1),0);
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        Buchung buchung = Buchung.neueBuchung(buchungsId, gast, zeitraum);

        Zimmer zimmer = new Zimmer(zimmerId, Zimmerkategorie.DOPPELZIMMER_STANDARD);

        when(buchungRepository.findById(buchungsId)).thenReturn(Optional.of(buchung));
        when(zimmerRepository.findById(zimmerId)).thenReturn(Optional.of(zimmer));

        CheckeGastEin command = new CheckeGastEin(buchungsId, zimmerId);

        // Act
        BuchungsId result = buchungApplicationService.checkeGastEin(command);

        // Assert
        assertEquals(buchungsId, result);
        assertEquals(BuchungsStatus.EINGECHECKT, buchung.getStatus());
        assertEquals(zimmerId, buchung.getZimmerId().get());
        assertEquals(zimmer.istVerfuegbarFuer(zeitraum), false);

        verify(buchungRepository).update(buchung);
        verify(zimmerRepository).update(zimmer);
    }

    @Test
    void testCheckInZimmerBelegt() {
        // Arrange
        BuchungsId buchungsId = new BuchungsId(UUID.randomUUID());
        ZimmerId zimmerId = new ZimmerId(UUID.randomUUID());

        HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1), 0);
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        Buchung buchung = Buchung.neueBuchung(buchungsId, gast, zeitraum);

        Zimmer zimmer = new Zimmer(zimmerId, Zimmerkategorie.DOPPELZIMMER_STANDARD);
        BuchungsId bestehendeBuchungId = new BuchungsId(UUID.randomUUID());
        zimmer.belegeFuer(bestehendeBuchungId, zeitraum);

        when(buchungRepository.findById(buchungsId)).thenReturn(Optional.of(buchung));
        when(zimmerRepository.findById(zimmerId)).thenReturn(Optional.of(zimmer));

        CheckeGastEin command = new CheckeGastEin(buchungsId, zimmerId);

        // Act & Assert
        assertThrows(ZimmerNichtVerfuegbarException.class, () -> buchungApplicationService.checkeGastEin(command));

        verify(buchungRepository, never()).update(any());
        verify(zimmerRepository, never()).update(any());
    }

    @Test
    void testCheckInZimmerInWartung() {
        // Arrange
        BuchungsId buchungsId = new BuchungsId(UUID.randomUUID());
        ZimmerId zimmerId = new ZimmerId(UUID.randomUUID());

        HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1), 0);
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        Buchung buchung = Buchung.neueBuchung(buchungsId, gast, zeitraum);

        Zimmer zimmer = new Zimmer(zimmerId, Zimmerkategorie.DOPPELZIMMER_STANDARD);
        zimmer.planeWartung(zeitraum);

        when(buchungRepository.findById(buchungsId)).thenReturn(Optional.of(buchung));
        when(zimmerRepository.findById(zimmerId)).thenReturn(Optional.of(zimmer));

        CheckeGastEin command = new CheckeGastEin(buchungsId, zimmerId);

        // Act & Assert
        assertThrows(ZimmerNichtVerfuegbarException.class, () -> buchungApplicationService.checkeGastEin(command));
    }
}
