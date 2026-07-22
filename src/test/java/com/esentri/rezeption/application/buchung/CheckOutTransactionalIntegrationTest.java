package com.esentri.rezeption.application.buchung;

import com.esentri.rezeption.domain.Zeitraum;
import com.esentri.rezeption.domain.buchung.*;
import com.esentri.rezeption.domain.zimmer.Zimmer;
import com.esentri.rezeption.domain.zimmer.ZimmerId;
import com.esentri.rezeption.domain.zimmer.ZimmerRepository;
import com.esentri.rezeption.domain.zimmer.Zimmerkategorie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CheckOutTransactionalIntegrationTest {

    @Autowired
    private BuchungApplicationService buchungApplicationService;

    @Autowired
    private BuchungRepository buchungRepository;

    @Autowired
    private ZimmerRepository zimmerRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void testCheckOutTransactionalSuccess() {
        // Arrange
        BuchungsId buchungsId = new BuchungsId(UUID.randomUUID());
        ZimmerId zimmerId = new ZimmerId(UUID.randomUUID());
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));

        prepareData(buchungsId, zimmerId, zeitraum);

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.executeWithoutResult(status -> {
            // Act
            CheckeGastAus command = new CheckeGastAus(buchungsId);
            buchungApplicationService.checkeGastAus(command);
        });

        // Assert
        Buchung updatedBuchung = buchungRepository.findById(buchungsId).orElseThrow();
        assertEquals(BuchungsStatus.AUSGECHECKT, updatedBuchung.getStatus());

        Zimmer updatedZimmer = zimmerRepository.findById(zimmerId).orElseThrow();
        // Das Zimmer sollte nun wieder verfügbar sein, da das Event verarbeitet wurde
        assertTrue(updatedZimmer.istVerfuegbarFuer(zeitraum), "Zimmer sollte nach erfolgreichem Checkout wieder verfuegbar sein");
    }

    @Test
    void testCheckOutTransactionalRollback() {
        // Arrange
        BuchungsId buchungsId = new BuchungsId(UUID.randomUUID());
        ZimmerId zimmerId = new ZimmerId(UUID.randomUUID());
        Zeitraum zeitraum = new Zeitraum(LocalDate.now(), LocalDate.now().plusDays(2));

        prepareData(buchungsId, zimmerId, zeitraum);

        // Act & Assert
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        assertThrows(RuntimeException.class, () -> {
            transactionTemplate.executeWithoutResult(status -> {
                CheckeGastAus command = new CheckeGastAus(buchungsId);
                buchungApplicationService.checkeGastAus(command);

                // Wir erzwingen ein Rollback
                throw new RuntimeException("Rollback erzwingen");
            });
        });

        // Assert
        Buchung buchung = buchungRepository.findById(buchungsId).orElseThrow();
        // Status sollte noch EINGECHECKT sein wegen Rollback
        assertEquals(BuchungsStatus.EINGECHECKT, buchung.getStatus());

        Zimmer zimmer = zimmerRepository.findById(zimmerId).orElseThrow();
        // Das Zimmer sollte IMMER NOCH BELEGT sein, da das Event nicht verarbeitet werden durfte
        assertFalse(zimmer.istVerfuegbarFuer(zeitraum), "Zimmer sollte nach Rollback immer noch belegt sein");
    }

    private void prepareData(BuchungsId buchungsId, ZimmerId zimmerId, Zeitraum zeitraum) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.executeWithoutResult(status -> {
            // Zimmer anlegen und belegen
            Zimmer zimmer = new Zimmer(zimmerId, Zimmerkategorie.DOPPELZIMMER_STANDARD);
            zimmer.belegeFuer(buchungsId, zeitraum);
            zimmerRepository.insert(zimmer);

            // Buchung anlegen und einchecken
            HauptGast gast = new HauptGast(new HauptGastId(UUID.randomUUID()), "John", "Doe", LocalDate.of(1990, 1, 1), 0);
            Buchung buchung = Buchung.neueBuchung(buchungsId, gast, zeitraum);
            buchung.checkeEin(zimmerId);
            buchungRepository.insert(buchung);
        });
    }
}
