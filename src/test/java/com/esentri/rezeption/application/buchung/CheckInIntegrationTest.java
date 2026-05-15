package com.esentri.rezeption.application.buchung;

import com.esentri.rezeption.domain.ZimmerId;
import com.esentri.rezeption.domain.buchung.*;
import com.esentri.rezeption.domain.zimmer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
    private CheckInApplicationService checkInApplicationService;

    @BeforeEach
    void setUp() {
        buchungRepository = mock(BuchungRepository.class);
        zimmerRepository = mock(ZimmerRepository.class);
        checkInService = new CheckInService(buchungRepository, zimmerRepository);
        checkInApplicationService = new CheckInApplicationService(checkInService);
    }

    @Test
    void testErfolgreicherCheckIn() {
        // Arrange
        BuchungsId buchungsId = new BuchungsId(UUID.randomUUID());
        ZimmerId zimmerId = new ZimmerId(UUID.randomUUID());

        HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1));
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        Buchung buchung = Buchung.neueBuchung(buchungsId, gast, zeitraum);

        Zimmer zimmer = new Zimmer(zimmerId, Zimmerkategorie.DOPPELZIMMER_STANDARD, ZimmerStatus.FREI);

        when(buchungRepository.findById(buchungsId)).thenReturn(Optional.of(buchung));
        when(zimmerRepository.findById(zimmerId)).thenReturn(Optional.of(zimmer));

        CheckeGastEin command = new CheckeGastEin(buchungsId, zimmerId);

        // Act
        BuchungsId result = checkInApplicationService.checkeGastEin(command);

        // Assert
        assertEquals(buchungsId, result);
        assertEquals(BuchungsStatus.EINGECHECKT, buchung.getStatus());
        assertEquals(zimmerId, buchung.getZimmerId().get());
        assertEquals(ZimmerStatus.BELEGT, zimmer.getStatus());

        verify(buchungRepository).update(buchung);
        verify(zimmerRepository).update(zimmer);
    }

    @Test
    void testCheckInZimmerBelegt() {
        // Arrange
        BuchungsId buchungsId = new BuchungsId(UUID.randomUUID());
        ZimmerId zimmerId = new ZimmerId(UUID.randomUUID());

        HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1));
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        Buchung buchung = Buchung.neueBuchung(buchungsId, gast, zeitraum);

        Zimmer zimmer = new Zimmer(zimmerId, Zimmerkategorie.DOPPELZIMMER_STANDARD, ZimmerStatus.BELEGT);

        when(buchungRepository.findById(buchungsId)).thenReturn(Optional.of(buchung));
        when(zimmerRepository.findById(zimmerId)).thenReturn(Optional.of(zimmer));

        CheckeGastEin command = new CheckeGastEin(buchungsId, zimmerId);

        // Act & Assert
        assertThrows(ZimmerNichtVerfuegbarException.class, () -> checkInApplicationService.checkeGastEin(command));

        verify(buchungRepository, never()).update(any());
        verify(zimmerRepository, never()).update(any());
    }

    @Test
    void testCheckInZimmerInWartung() {
        // Arrange
        BuchungsId buchungsId = new BuchungsId(UUID.randomUUID());
        ZimmerId zimmerId = new ZimmerId(UUID.randomUUID());

        HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1));
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));
        Buchung buchung = Buchung.neueBuchung(buchungsId, gast, zeitraum);

        Zimmer zimmer = new Zimmer(zimmerId, Zimmerkategorie.DOPPELZIMMER_STANDARD, ZimmerStatus.WARTUNG);

        when(buchungRepository.findById(buchungsId)).thenReturn(Optional.of(buchung));
        when(zimmerRepository.findById(zimmerId)).thenReturn(Optional.of(zimmer));

        CheckeGastEin command = new CheckeGastEin(buchungsId, zimmerId);

        // Act & Assert
        assertThrows(ZimmerNichtVerfuegbarException.class, () -> checkInApplicationService.checkeGastEin(command));
    }
}
