package com.esentri.hotel.rezeption.core.application;

import com.esentri.hotel.rezeption.core.domain.NichtGefundenException;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungRepository;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungsStatus;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckOutService;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeAus;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeEin;
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

import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.ANREISE;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.HOTEL;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.ZEITRAUM;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.gastdaten;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.reservierungOhneKontaktdaten;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Aufenthalt jeweils 02.-05.11., CheckIn am 02.11. Der Checkout-Tag wird direkt an den {@link CheckOutService}
 * uebergeben, um vorzeitige, regulaere und verspaetete Abreise zu pruefen.
 */
@SpringBootTest
@ActiveProfiles("test")
class CheckOutIntegrationTest {

    @Autowired
    BuchungApplicationService buchungApplicationService;

    @Autowired
    ZimmerApplicationService zimmerApplicationService;

    @Autowired
    CheckOutService checkOutService;

    @Autowired
    BuchungRepository buchungRepository;

    @Autowired
    ZimmerRepository zimmerRepository;

    @Test
    void regulaererCheckoutAmAbreisetag() {
        var zimmerId = legeZimmerAn("CO-105");
        var buchungsId = checkeEin("CO-5", "CO-105");

        checkOutService.checkeAus(new CheckeAus(HOTEL, new Zimmernummer("CO-105")), ZEITRAUM.abreise());

        assertThat(buchungRepository.findById(buchungsId).orElseThrow().getStatus())
            .isEqualTo(BuchungsStatus.AUSGECHECKT);
        assertThat(belegungszeitraum(zimmerId))
            .isEqualTo(new Belegungszeitraum(ZEITRAUM.anreise(), ZEITRAUM.abreise()));
    }

    /** Die Uhr der Tests steht auf dem Anreisetag. */
    @Test
    void abreiseAmAnreisetagGibtZimmerKomplettFrei() {
        var zimmerId = legeZimmerAn("CO-101");
        var buchungsId = checkeEin("CO-1", "CO-101");

        buchungApplicationService.checkeAus(new CheckeAus(HOTEL, new Zimmernummer("CO-101")));

        var buchung = buchungRepository.findById(buchungsId).orElseThrow();
        assertThat(buchung.getStatus()).isEqualTo(BuchungsStatus.AUSGECHECKT);
        assertThat(buchung.getAusgechecktAm()).contains(ANREISE);
        assertThat(zimmerRepository.findById(zimmerId).orElseThrow().getBelegungen()).isEmpty();
    }

    @Test
    void vorzeitigeAbreiseGibtZimmerAbHeuteFrei() {
        var zimmerId = legeZimmerAn("CO-102");
        checkeEin("CO-2", "CO-102");

        checkOutService.checkeAus(new CheckeAus(HOTEL, new Zimmernummer("CO-102")), ANREISE.plusDays(1));

        assertThat(belegungszeitraum(zimmerId)).isEqualTo(new Belegungszeitraum(ANREISE, ANREISE.plusDays(1)));
    }

    @Test
    void verspaeteteAbreiseLaesstBelegungUnveraendert() {
        var zimmerId = legeZimmerAn("CO-103");
        var buchungsId = checkeEin("CO-3", "CO-103");

        checkOutService.checkeAus(new CheckeAus(HOTEL, new Zimmernummer("CO-103")), ZEITRAUM.abreise().plusDays(1));

        assertThat(belegungszeitraum(zimmerId))
            .isEqualTo(new Belegungszeitraum(ZEITRAUM.anreise(), ZEITRAUM.abreise()));
        assertThat(buchungRepository.findById(buchungsId).orElseThrow().getAusgechecktAm())
            .contains(ZEITRAUM.abreise().plusDays(1));
    }

    @Test
    void ohneEingechecktenGastKeinCheckout() {
        legeZimmerAn("CO-104");

        assertThatThrownBy(() -> buchungApplicationService.checkeAus(new CheckeAus(HOTEL, new Zimmernummer("CO-104"))))
            .isInstanceOf(NichtGefundenException.class)
            .hasMessageContaining("kein Gast eingecheckt");
        assertThatThrownBy(() -> buchungApplicationService.checkeAus(
            new CheckeAus(new HotelId(java.util.UUID.randomUUID()), new Zimmernummer("CO-104"))))
            .isInstanceOf(NichtGefundenException.class);
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

    private Belegungszeitraum belegungszeitraum(Zimmer.ZimmerId zimmerId) {
        return zimmerRepository.findById(zimmerId).orElseThrow().getBelegungen().stream()
            .map(Belegung::getZeitraum).findFirst().orElseThrow();
    }
}
