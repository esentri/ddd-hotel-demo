package com.esentri.hotel.rezeption.core.application;

import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
import com.esentri.hotel.rezeption.core.domain.NichtGefundenException;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungRepository;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungsStatus;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeEin;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeSpontanEin;
import com.esentri.hotel.rezeption.core.domain.zimmer.BeendeWartung;
import com.esentri.hotel.rezeption.core.domain.zimmer.Belegung;
import com.esentri.hotel.rezeption.core.domain.zimmer.Belegungsart;
import com.esentri.hotel.rezeption.core.domain.zimmer.Belegungszeitraum;
import com.esentri.hotel.rezeption.core.domain.zimmer.PlaneWartung;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.LegeZimmerAn;
import com.esentri.hotel.rezeption.core.domain.zimmer.Wartungsgrund;
import com.esentri.hotel.rezeption.core.domain.zimmer.ZimmerRepository;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;
import io.domainlifecycles.assertion.DomainAssertionException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.ANDERES_HOTEL;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.ANREISE;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.HOTEL;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.ZEITRAUM;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.gastdaten;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.reservierungOhneKontaktdaten;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class CheckInIntegrationTest {

    @Autowired
    BuchungApplicationService buchungApplicationService;

    @Autowired
    ZimmerApplicationService zimmerApplicationService;

    @Autowired
    BuchungRepository buchungRepository;

    @Autowired
    ZimmerRepository zimmerRepository;

    @Test
    void checkInBelegtZimmerUndAktualisiertBuchung() {
        var zimmerId = zimmerApplicationService.legeZimmerAn(new LegeZimmerAn(HOTEL, new Zimmernummer("CI-101"), Zimmerkategorie.DOPPELZIMMER));
        var buchungsId = buchungApplicationService.uebernimmReservierung(reservierungOhneKontaktdaten("CI-1"));

        buchungApplicationService.checkeEin(new CheckeEin(buchungsId, new Zimmernummer("CI-101"), gastdaten()));

        var buchung = buchungRepository.findById(buchungsId).orElseThrow();
        assertThat(buchung.getStatus()).isEqualTo(BuchungsStatus.EINGECHECKT);
        assertThat(buchung.getZimmerId()).contains(zimmerId);
        assertThat(buchung.getHauptgast().getAdresse()).contains(gastdaten().adresse());
        assertGastBelegung(zimmerId, buchungsId);
    }

    @Test
    void belegtesZimmerKannNichtEingechecktWerden() {
        zimmerApplicationService.legeZimmerAn(new LegeZimmerAn(HOTEL, new Zimmernummer("CI-102"), Zimmerkategorie.DOPPELZIMMER));
        var erste = buchungApplicationService.uebernimmReservierung(reservierungOhneKontaktdaten("CI-2"));
        var zweite = buchungApplicationService.uebernimmReservierung(reservierungOhneKontaktdaten("CI-3"));
        buchungApplicationService.checkeEin(new CheckeEin(erste, new Zimmernummer("CI-102"), gastdaten()));

        assertThatThrownBy(() -> buchungApplicationService.checkeEin(
            new CheckeEin(zweite, new Zimmernummer("CI-102"), gastdaten())))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("bereits belegt");
        assertThat(buchungRepository.findById(zweite).orElseThrow().getStatus()).isEqualTo(BuchungsStatus.RESERVIERT);
    }

    @Test
    void geplanteWartungImAufenthaltVerhindertCheckIn() {
        var zimmernummer = new Zimmernummer("CI-106");
        zimmerApplicationService.legeZimmerAn(new LegeZimmerAn(HOTEL, zimmernummer, Zimmerkategorie.DOPPELZIMMER));
        // Aufenthalt 02.-05.11., Wartung ab dem 04.11.
        var wartung = zimmerApplicationService.planeWartung(new PlaneWartung(HOTEL, zimmernummer,
            new Wartungsgrund("Heizung"), new Belegungszeitraum(ANREISE.plusDays(2), ANREISE.plusDays(4))));
        var buchungsId = buchungApplicationService.uebernimmReservierung(reservierungOhneKontaktdaten("CI-6"));

        assertThatThrownBy(() -> buchungApplicationService.checkeEin(
            new CheckeEin(buchungsId, zimmernummer, gastdaten())))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("WARTUNG");
        assertThat(buchungRepository.findById(buchungsId).orElseThrow().getStatus())
            .isEqualTo(BuchungsStatus.RESERVIERT);

        zimmerApplicationService.beendeWartung(new BeendeWartung(HOTEL, zimmernummer, wartung));
        buchungApplicationService.checkeEin(new CheckeEin(buchungsId, zimmernummer, gastdaten()));
        assertThat(buchungRepository.findById(buchungsId).orElseThrow().getStatus())
            .isEqualTo(BuchungsStatus.EINGECHECKT);
    }

    @Test
    void wartungNachDemAufenthaltErlaubtCheckIn() {
        var zimmernummer = new Zimmernummer("CI-107");
        var zimmerId = zimmerApplicationService.legeZimmerAn(new LegeZimmerAn(HOTEL, zimmernummer, Zimmerkategorie.DOPPELZIMMER));
        zimmerApplicationService.planeWartung(new PlaneWartung(HOTEL, zimmernummer,
            new Wartungsgrund("Maler"), new Belegungszeitraum(ZEITRAUM.abreise(), ZEITRAUM.abreise().plusDays(2))));
        var buchungsId = buchungApplicationService.uebernimmReservierung(reservierungOhneKontaktdaten("CI-7"));

        buchungApplicationService.checkeEin(new CheckeEin(buchungsId, zimmernummer, gastdaten()));

        var belegungen = zimmerRepository.findById(zimmerId).orElseThrow().getBelegungen();
        assertThat(belegungen).extracting(Belegung::getArt)
            .containsExactlyInAnyOrder(Belegungsart.WARTUNG, Belegungsart.GAST);
        assertThat(belegungen).filteredOn(b -> b.getArt() == Belegungsart.WARTUNG).singleElement()
            .extracting(b -> b.getWartungsgrund().orElseThrow()).isEqualTo(new Wartungsgrund("Maler"));
    }

    private void assertGastBelegung(Zimmer.ZimmerId zimmerId, Buchung.BuchungsId buchungsId) {
        assertThat(zimmerRepository.findById(zimmerId).orElseThrow().getBelegungen()).singleElement()
            .satisfies(b -> {
                assertThat(b.getArt()).isEqualTo(Belegungsart.GAST);
                assertThat(b.getBuchungsId()).contains(buchungsId);
                assertThat(b.getZeitraum()).isEqualTo(new Belegungszeitraum(ZEITRAUM.anreise(), ZEITRAUM.abreise()));
            });
    }

    @Test
    void checkInNurInZimmerDerGebuchtenKategorie() {
        zimmerApplicationService.legeZimmerAn(
            new LegeZimmerAn(HOTEL, new Zimmernummer("CI-108"), Zimmerkategorie.EINZELZIMMER));
        var buchungsId = buchungApplicationService.uebernimmReservierung(reservierungOhneKontaktdaten("CI-8"));

        assertThatThrownBy(() -> buchungApplicationService.checkeEin(
            new CheckeEin(buchungsId, new Zimmernummer("CI-108"), gastdaten())))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("Gebucht ist ein DOPPELZIMMER, das Zimmer ist ein EINZELZIMMER");
    }

    @Test
    void spontaneBuchungUebernimmtKategorieDesZimmers() {
        zimmerApplicationService.legeZimmerAn(
            new LegeZimmerAn(HOTEL, new Zimmernummer("CI-109"), Zimmerkategorie.EINZELZIMMER));

        var buchungsId = buchungApplicationService.checkeSpontanEin(
            new CheckeSpontanEin(HOTEL, new Zimmernummer("CI-109"), gastdaten(), ZEITRAUM));

        assertThat(buchungRepository.findById(buchungsId).orElseThrow().getZimmerkategorie())
            .isEqualTo(Zimmerkategorie.EINZELZIMMER);
    }

    @Test
    void zimmernummerWirdNurImHotelDerBuchungGesucht() {
        zimmerApplicationService.legeZimmerAn(new LegeZimmerAn(ANDERES_HOTEL, new Zimmernummer("CI-103"), Zimmerkategorie.DOPPELZIMMER));
        var buchungsId = buchungApplicationService.uebernimmReservierung(reservierungOhneKontaktdaten("CI-4"));

        assertThatThrownBy(() -> buchungApplicationService.checkeEin(
            new CheckeEin(buchungsId, new Zimmernummer("CI-103"), gastdaten())))
            .isInstanceOf(NichtGefundenException.class);
    }

    @Test
    void gleicheZimmernummerInVerschiedenenHotels() {
        var imHotel = zimmerApplicationService.legeZimmerAn(new LegeZimmerAn(HOTEL, new Zimmernummer("CI-104"), Zimmerkategorie.DOPPELZIMMER));
        var imAnderenHotel = zimmerApplicationService.legeZimmerAn(
            new LegeZimmerAn(ANDERES_HOTEL, new Zimmernummer("CI-104"), Zimmerkategorie.DOPPELZIMMER));

        assertThat(imHotel).isNotEqualTo(imAnderenHotel);
        assertThatThrownBy(() -> zimmerApplicationService.legeZimmerAn(
            new LegeZimmerAn(HOTEL, new Zimmernummer("CI-104"), Zimmerkategorie.DOPPELZIMMER)))
            .isInstanceOf(ZimmerBereitsVorhandenException.class);
    }

    @Test
    void spontanerCheckInLegtEingecheckteBuchungAnUndBelegtZimmer() {
        var zimmerId = zimmerApplicationService.legeZimmerAn(new LegeZimmerAn(HOTEL, new Zimmernummer("CI-105"), Zimmerkategorie.DOPPELZIMMER));

        var buchungsId = buchungApplicationService.checkeSpontanEin(
            new CheckeSpontanEin(HOTEL, new Zimmernummer("CI-105"), gastdaten(), ZEITRAUM));

        var buchung = buchungRepository.findById(buchungsId).orElseThrow();
        assertThat(buchung.getStatus()).isEqualTo(BuchungsStatus.EINGECHECKT);
        assertThat(buchung.getZimmerId()).contains(zimmerId);
        assertGastBelegung(zimmerId, buchungsId);
    }
}
