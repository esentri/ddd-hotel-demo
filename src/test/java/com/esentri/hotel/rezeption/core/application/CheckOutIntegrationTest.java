package com.esentri.hotel.rezeption.core.application;

import com.esentri.hotel.rezeption.core.domain.NichtGefundenException;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungRepository;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungsStatus;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeAus;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeEin;
import com.esentri.hotel.rezeption.core.domain.buchung.GastAusgecheckt;
import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import com.esentri.hotel.rezeption.core.domain.zimmer.Belegung;
import com.esentri.hotel.rezeption.core.domain.zimmer.Belegungszeitraum;
import com.esentri.hotel.rezeption.core.domain.zimmer.LegeZimmerAn;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.ZimmerRepository;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.ANREISE;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.HOTEL;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.ZEITRAUM;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.gastdaten;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.reservierungOhneKontaktdaten;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

/**
 * Aufenthalt jeweils 02.-05.11., CheckIn am 02.11. (Uhr der Tests). Der Checkout aendert sofort nur die Buchung;
 * das Zimmer wird asynchron nach dem Commit ueber {@link GastAusgecheckt} freigegeben.
 */
@SpringBootTest
@ActiveProfiles("test")
class CheckOutIntegrationTest {

    private static final Duration EVENT_TIMEOUT = Duration.ofSeconds(5);

    @Autowired
    BuchungApplicationService buchungApplicationService;

    @Autowired
    ZimmerApplicationService zimmerApplicationService;

    @Autowired
    BuchungRepository buchungRepository;

    @Autowired
    ZimmerRepository zimmerRepository;

    // --- Checkout bis zur Freigabe des Zimmers per Event

    @Test
    void checkoutGibtZimmerPerEventFrei() {
        var zimmerId = legeZimmerAn("CO-101");
        var buchungsId = checkeEin("CO-1", "CO-101");

        buchungApplicationService.checkeAus(new CheckeAus(HOTEL, new Zimmernummer("CO-101")));

        var buchung = buchungRepository.findById(buchungsId).orElseThrow();
        assertThat(buchung.getStatus()).isEqualTo(BuchungsStatus.AUSGECHECKT);
        assertThat(buchung.getAusgechecktAm()).contains(ANREISE);
        // Abreise am Anreisetag: die Gast-Belegung entfaellt, sobald das Event verarbeitet ist
        await().atMost(EVENT_TIMEOUT).untilAsserted(() ->
            assertThat(zimmerRepository.findById(zimmerId).orElseThrow().getBelegungen()).isEmpty());
    }

    @Test
    void ohneEingechecktenGastKeinCheckout() {
        legeZimmerAn("CO-104");

        assertThatThrownBy(() -> buchungApplicationService.checkeAus(new CheckeAus(HOTEL, new Zimmernummer("CO-104"))))
            .isInstanceOf(NichtGefundenException.class)
            .hasMessageContaining("kein Gast eingecheckt");
        assertThatThrownBy(() -> buchungApplicationService.checkeAus(
            new CheckeAus(new HotelId(UUID.randomUUID()), new Zimmernummer("CO-104"))))
            .isInstanceOf(NichtGefundenException.class);
    }

    // --- Reaktion des Zimmers auf GastAusgecheckt (Listener direkt mit beliebigem Checkout-Tag aufgerufen)

    @Test
    void vorzeitigeAbreiseGibtZimmerAbCheckoutTagFrei() {
        var zimmerId = legeZimmerAn("CO-102");
        var buchungsId = checkeEin("CO-2", "CO-102");

        zimmerApplicationService.onGastAusgecheckt(gastAusgecheckt(buchungsId, zimmerId, ANREISE.plusDays(1)));

        assertThat(belegungszeitraum(zimmerId)).isEqualTo(new Belegungszeitraum(ANREISE, ANREISE.plusDays(1)));
    }

    @Test
    void regulaereAbreiseLaesstBelegungUnveraendert() {
        var zimmerId = legeZimmerAn("CO-105");
        var buchungsId = checkeEin("CO-5", "CO-105");

        zimmerApplicationService.onGastAusgecheckt(gastAusgecheckt(buchungsId, zimmerId, ZEITRAUM.abreise()));

        assertThat(belegungszeitraum(zimmerId))
            .isEqualTo(new Belegungszeitraum(ZEITRAUM.anreise(), ZEITRAUM.abreise()));
    }

    @Test
    void verspaeteteAbreiseLaesstBelegungUnveraendert() {
        var zimmerId = legeZimmerAn("CO-103");
        var buchungsId = checkeEin("CO-3", "CO-103");

        zimmerApplicationService.onGastAusgecheckt(
            gastAusgecheckt(buchungsId, zimmerId, ZEITRAUM.abreise().plusDays(1)));

        assertThat(belegungszeitraum(zimmerId))
            .isEqualTo(new Belegungszeitraum(ZEITRAUM.anreise(), ZEITRAUM.abreise()));
    }

    private Zimmer.ZimmerId legeZimmerAn(String zimmernummer) {
        return zimmerApplicationService.legeZimmerAn(
            new LegeZimmerAn(HOTEL, new Zimmernummer(zimmernummer), Zimmerkategorie.DOPPELZIMMER));
    }

    private Buchung.BuchungsId checkeEin(String reservierungsnummer, String zimmernummer) {
        var buchungsId = buchungApplicationService.uebernimmReservierung(
            reservierungOhneKontaktdaten(reservierungsnummer));
        buchungApplicationService.checkeEin(new CheckeEin(buchungsId, new Zimmernummer(zimmernummer), gastdaten()));
        return buchungsId;
    }

    private static GastAusgecheckt gastAusgecheckt(Buchung.BuchungsId buchungsId, Zimmer.ZimmerId zimmerId,
                                                   LocalDate ausgechecktAm) {
        return new GastAusgecheckt(buchungsId, HOTEL, zimmerId, ausgechecktAm);
    }

    private Belegungszeitraum belegungszeitraum(Zimmer.ZimmerId zimmerId) {
        return zimmerRepository.findById(zimmerId).orElseThrow().getBelegungen().stream()
            .map(Belegung::getZeitraum).findFirst().orElseThrow();
    }
}
