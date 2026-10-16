package com.esentri.hotel.rezeption;

import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.zimmer.Bettart;
import com.esentri.hotel.rezeption.core.domain.zimmer.Blickrichtung;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.ZimmerRepository;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerstatus;
import io.domainlifecycles.persistence.exception.DLCPersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static com.esentri.hotel.rezeption.RezeptionApi.neueZimmernummer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Zwei Rezeptionisten weisen gleichzeitig dasselbe Zimmer zu: beide haben das Zimmer im Zustand "bezugsfertig"
 * geladen, die fachliche Pruefung ist fuer beide erfolgreich. Erst das optimistische Locking (concurrencyVersion)
 * verhindert beim Speichern die Doppelbelegung.
 */
@SpringBootTest
class OptimistischesLockingIntegrationTest {

    @Autowired
    private ZimmerRepository zimmerRepository;

    @Test
    void gleichzeitigeBelegungDesselbenZimmersScheitertBeimZweitenSpeichern() {
        var zimmerId = legeBezugsfertigesZimmerAn();
        var buchungRezeptionistA = new Buchung.BuchungId(UUID.randomUUID());
        var buchungRezeptionistB = new Buchung.BuchungId(UUID.randomUUID());

        var zimmerBeiRezeptionistA = zimmerRepository.findById(zimmerId).orElseThrow();
        var zimmerBeiRezeptionistB = zimmerRepository.findById(zimmerId).orElseThrow();

        zimmerBeiRezeptionistA.belege(buchungRezeptionistA, Zimmerkategorie.DOPPELZIMMER);
        zimmerBeiRezeptionistB.belege(buchungRezeptionistB, Zimmerkategorie.DOPPELZIMMER);

        zimmerRepository.update(zimmerBeiRezeptionistA);

        assertThatThrownBy(() -> zimmerRepository.update(zimmerBeiRezeptionistB))
            .isInstanceOf(DLCPersistenceException.class)
            .hasMessageContaining("Optimistic locking conflict");

        var gespeichert = zimmerRepository.findById(zimmerId).orElseThrow();
        assertThat(gespeichert.getStatus()).isEqualTo(Zimmerstatus.BELEGT);
        assertThat(gespeichert.getBelegtDurch()).isEqualTo(buchungRezeptionistA);
    }

    private Zimmer.ZimmerId legeBezugsfertigesZimmerAn() {
        var zimmer = Zimmer.builder()
            .id(new Zimmer.ZimmerId(UUID.randomUUID()))
            .zimmernummer(new Zimmernummer(neueZimmernummer()))
            .zimmerkategorie(Zimmerkategorie.DOPPELZIMMER)
            .etage(3)
            .blickrichtung(Blickrichtung.INNENHOF)
            .balkon(false)
            .barrierefrei(true)
            .bettart(Bettart.ZWEI_EINZELBETTEN)
            .raucherzimmer(false)
            .status(Zimmerstatus.BEZUGSFERTIG)
            .build();
        return zimmerRepository.insert(zimmer).getId();
    }
}
