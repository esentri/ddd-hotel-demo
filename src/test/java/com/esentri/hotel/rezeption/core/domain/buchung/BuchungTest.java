package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.DomainTestSupport;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import io.domainlifecycles.assertion.DomainAssertionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.ANREISE;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.HOTEL;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.ZEITRAUM;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.gastdaten;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.reservierung;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.reservierungOhneKontaktdaten;
import static com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie.DOPPELZIMMER;
import static com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie.EINZELZIMMER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BuchungTest {

    @BeforeAll
    static void initDomain() {
        DomainTestSupport.initDomain();
    }

    // --- Reservierung uebernehmen

    @Test
    void uebernommeneReservierungIstReserviertOhneZimmer() {
        var buchung = Buchung.uebernimmReservierung(reservierung("R-1"));

        assertThat(buchung.getStatus()).isEqualTo(BuchungsStatus.RESERVIERT);
        assertThat(buchung.getHotelId()).isEqualTo(HOTEL);
        assertThat(buchung.getZimmerId()).isEmpty();
        assertThat(buchung.getReservierungsnummer()).contains(new Reservierungsnummer("R-1"));
        assertThat(buchung.getHauptgast().getName()).isEqualTo(new Name("Erika", "Mustermann"));
    }

    @Test
    void kontaktdatenDesHauptgastsDuerfenBeiDerUebernahmeFehlen() {
        var buchung = Buchung.uebernimmReservierung(reservierungOhneKontaktdaten("R-2"));

        assertThat(buchung.getHauptgast().getEmail()).isEmpty();
        assertThat(buchung.getHauptgast().getAdresse()).isEmpty();
    }

    @Test
    void buchungOhneHauptgastIstUngueltig() {
        assertThatThrownBy(() -> new Buchung(0, neueBuchungsId(), HOTEL, new Reservierungsnummer("R-3"), null,
            ZEITRAUM, DOPPELZIMMER, null, BuchungsStatus.RESERVIERT, null))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void buchungOhneHotelIstUngueltig() {
        assertThatThrownBy(() -> new Buchung(0, neueBuchungsId(), null, new Reservierungsnummer("R-3"),
            gastOhneAdresse(), ZEITRAUM, DOPPELZIMMER, null, BuchungsStatus.RESERVIERT, null))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void eingecheckteBuchungBrauchtEinZimmer() {
        assertThatThrownBy(() -> new Buchung(0, neueBuchungsId(), HOTEL, null, gastMitAdresse(), ZEITRAUM, DOPPELZIMMER,
            null, BuchungsStatus.EINGECHECKT, null))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void reservierteBuchungHatNochKeinZimmer() {
        assertThatThrownBy(() -> new Buchung(0, neueBuchungsId(), HOTEL, new Reservierungsnummer("R-4"),
            gastOhneAdresse(), ZEITRAUM, DOPPELZIMMER, neueZimmerId(), BuchungsStatus.RESERVIERT, null))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void eingecheckteBuchungBrauchtDieAdresseDesHauptgasts() {
        assertThatThrownBy(() -> new Buchung(0, neueBuchungsId(), HOTEL, null, gastOhneAdresse(), ZEITRAUM,
            DOPPELZIMMER, neueZimmerId(), BuchungsStatus.EINGECHECKT, null))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("Adresse");
    }

    // --- CheckIn

    @Test
    void checkInWeistZimmerZuUndUebernimmtGastdaten() {
        var buchung = Buchung.uebernimmReservierung(reservierungOhneKontaktdaten("R-5"));
        var zimmerId = neueZimmerId();

        buchung.checkeEin(zimmerId, DOPPELZIMMER, gastdaten(), ANREISE);

        assertThat(buchung.getStatus()).isEqualTo(BuchungsStatus.EINGECHECKT);
        assertThat(buchung.getZimmerId()).contains(zimmerId);
        assertThat(buchung.getHauptgast().getEmail()).contains(new Email("max@example.com"));
        assertThat(buchung.getHauptgast().getAdresse()).isEqualTo(java.util.Optional.of(gastdaten().adresse()));
    }

    @Test
    void checkInNurAmAnreisetag() {
        var buchung = Buchung.uebernimmReservierung(reservierung("R-6"));

        assertThatThrownBy(() -> buchung.checkeEin(neueZimmerId(), DOPPELZIMMER, gastdaten(), ANREISE.minusDays(1)))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("Anreisetag");
        assertThatThrownBy(() -> buchung.checkeEin(neueZimmerId(), DOPPELZIMMER, gastdaten(), ANREISE.plusDays(1)))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void eingecheckteBuchungKannNichtErneutEinchecken() {
        var buchung = Buchung.uebernimmReservierung(reservierung("R-7"));
        buchung.checkeEin(neueZimmerId(), DOPPELZIMMER, gastdaten(), ANREISE);

        assertThatThrownBy(() -> buchung.checkeEin(neueZimmerId(), DOPPELZIMMER, gastdaten(), ANREISE))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("reservierte");
    }

    @Test
    void gastdatenBeimCheckInBrauchenEineAdresse() {
        assertThatThrownBy(() -> new Gastdaten(new Name("Max", "Mustermann"), null, null, null))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void checkInNurInZimmerDerGebuchtenKategorie() {
        var buchung = Buchung.uebernimmReservierung(reservierung("R-9"));

        assertThatThrownBy(() -> buchung.checkeEin(neueZimmerId(), EINZELZIMMER, gastdaten(), ANREISE))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("Gebucht ist ein DOPPELZIMMER");
    }

    // --- Stornierung

    @Test
    void reservierteBuchungWirdStorniert() {
        var buchung = Buchung.uebernimmReservierung(reservierung("R-10"));

        buchung.storniere();

        assertThat(buchung.getStatus()).isEqualTo(BuchungsStatus.STORNIERT);
    }

    @Test
    void eingecheckteBuchungKannNichtStorniertWerden() {
        var buchung = Buchung.uebernimmReservierung(reservierung("R-11"));
        buchung.checkeEin(neueZimmerId(), DOPPELZIMMER, gastdaten(), ANREISE);

        assertThatThrownBy(buchung::storniere)
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("Nur reservierte");
    }

    @Test
    void stornierteBuchungKannWederEincheckenNochErneutStorniertWerden() {
        var buchung = Buchung.uebernimmReservierung(reservierung("R-12")).storniere();

        assertThatThrownBy(() -> buchung.checkeEin(neueZimmerId(), DOPPELZIMMER, gastdaten(), ANREISE))
            .isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(buchung::storniere)
            .isInstanceOf(DomainAssertionException.class);
    }

    // --- Checkout

    @Test
    void eingecheckteBuchungWirdAusgechecktUndBehaeltIhrZimmer() {
        var buchung = Buchung.uebernimmReservierung(reservierung("R-13"));
        var zimmerId = neueZimmerId();
        buchung.checkeEin(zimmerId, DOPPELZIMMER, gastdaten(), ANREISE);

        buchung.checkeAus(ANREISE.plusDays(3));

        assertThat(buchung.getStatus()).isEqualTo(BuchungsStatus.AUSGECHECKT);
        assertThat(buchung.getAusgechecktAm()).contains(ANREISE.plusDays(3));
        assertThat(buchung.getZimmerId()).contains(zimmerId);
    }

    @Test
    void nurEingecheckteBuchungenKoennenAuschecken() {
        var reserviert = Buchung.uebernimmReservierung(reservierung("R-14"));
        var ausgecheckt = Buchung.uebernimmReservierung(reservierung("R-15"));
        ausgecheckt.checkeEin(neueZimmerId(), DOPPELZIMMER, gastdaten(), ANREISE).checkeAus(ANREISE);

        assertThatThrownBy(() -> reserviert.checkeAus(ANREISE))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("Nur eingecheckte");
        assertThatThrownBy(() -> ausgecheckt.checkeAus(ANREISE))
            .isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(ausgecheckt::storniere)
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void ausgecheckteBuchungBrauchtCheckoutTagUndZimmer() {
        assertThatThrownBy(() -> new Buchung(0, neueBuchungsId(), HOTEL, null, gastMitAdresse(), ZEITRAUM,
            DOPPELZIMMER, neueZimmerId(), BuchungsStatus.AUSGECHECKT, null))
            .isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> new Buchung(0, neueBuchungsId(), HOTEL, null, gastMitAdresse(), ZEITRAUM,
            DOPPELZIMMER, null, BuchungsStatus.AUSGECHECKT, ANREISE))
            .isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> new Buchung(0, neueBuchungsId(), HOTEL, null, gastMitAdresse(), ZEITRAUM,
            DOPPELZIMMER, neueZimmerId(), BuchungsStatus.EINGECHECKT, ANREISE))
            .isInstanceOf(DomainAssertionException.class);
    }

    // --- Spontaner CheckIn

    @Test
    void spontanerCheckInErzeugtEingecheckteBuchungOhneReservierungsnummer() {
        var zimmerId = neueZimmerId();

        var buchung = Buchung.checkeSpontanEin(HOTEL, gastdaten(), ZEITRAUM, zimmerId, DOPPELZIMMER, ANREISE);

        assertThat(buchung.getStatus()).isEqualTo(BuchungsStatus.EINGECHECKT);
        assertThat(buchung.getZimmerId()).contains(zimmerId);
        assertThat(buchung.getReservierungsnummer()).isEmpty();
    }

    @Test
    void spontanerCheckInNurMitAnreiseHeute() {
        assertThatThrownBy(() -> Buchung.checkeSpontanEin(HOTEL, gastdaten(), ZEITRAUM, neueZimmerId(),
            DOPPELZIMMER, ANREISE.minusDays(1)))
            .isInstanceOf(DomainAssertionException.class);
    }

    // --- Wertobjekte

    @Test
    void anreiseMussVorAbreiseLiegen() {
        var tag = LocalDate.of(2026, 11, 2);
        assertThatThrownBy(() -> new Aufenthaltszeitraum(tag, tag))
            .isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> new Aufenthaltszeitraum(tag.plusDays(1), tag))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void hauptgastBrauchtVorUndNachname() {
        assertThatThrownBy(() -> new Name(" ", "Mustermann"))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void emailMussGueltigSein() {
        assertThatThrownBy(() -> new Email("keine-email"))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void reservierungsnummerDarfNichtLeerSein() {
        assertThatThrownBy(() -> new Reservierungsnummer(""))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void reservierungOhneAufenthaltszeitraumIstUngueltig() {
        assertThatThrownBy(() -> new UebernimmReservierung(HOTEL, new Reservierungsnummer("R-8"),
            new Name("Erika", "Mustermann"), null, null, null, null, DOPPELZIMMER))
            .isInstanceOf(DomainAssertionException.class);
    }

    private static Buchung.BuchungsId neueBuchungsId() {
        return new Buchung.BuchungsId(UUID.randomUUID());
    }

    private static Zimmer.ZimmerId neueZimmerId() {
        return new Zimmer.ZimmerId(UUID.randomUUID());
    }

    private static Gast gastOhneAdresse() {
        return new Gast(0, new Gast.GastId(UUID.randomUUID()), new Name("Erika", "Mustermann"), null, null, null);
    }

    private static Gast gastMitAdresse() {
        return new Gast(0, new Gast.GastId(UUID.randomUUID()), new Name("Erika", "Mustermann"), null, null,
            gastdaten().adresse());
    }
}
