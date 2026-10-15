package com.esentri.hotel.rezeption.core.domain.buchung;

import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
import io.domainlifecycles.assertion.DomainAssertionException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BuchungTest {

    private static final LocalDate HEUTE = LocalDate.of(2026, 10, 15);
    private static final LocalDate ERWACHSEN = LocalDate.of(1980, 5, 1);
    private static final Zimmer.ZimmerId ZIMMER = new Zimmer.ZimmerId(UUID.randomUUID());

    @Test
    void walkInLegtReservierteBuchungVorOrtAn() {
        var buchung = Buchung.walkIn(gast(ERWACHSEN), zeitraum(), Zimmerkategorie.SUITE);

        assertThat(buchung.getId()).isNotNull();
        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.RESERVIERT);
        assertThat(buchung.getZugewiesenesZimmer()).isNull();
    }

    @Test
    void checkinWeistZimmerZu() {
        var buchung = reserviert(gast(ERWACHSEN));

        buchung.checkeEin(HEUTE, ZIMMER);

        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.EINGECHECKT);
        assertThat(buchung.getZugewiesenesZimmer()).isEqualTo(ZIMMER);
    }

    @Test
    void checkinOhneZimmerIstNichtMoeglich() {
        var buchung = reserviert(gast(ERWACHSEN));

        assertThatThrownBy(() -> buchung.checkeEin(HEUTE, null))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("Zimmer");
        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.RESERVIERT);
    }

    @Test
    void buchungKannNurEinmalEingechecktWerden() {
        var buchung = reserviert(gast(ERWACHSEN));
        buchung.checkeEin(HEUTE, ZIMMER);

        assertThatThrownBy(() -> buchung.checkeEin(HEUTE, ZIMMER))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("bereits eingecheckt");
    }

    @Test
    void stornierteBuchungKannNichtEingechecktWerden() {
        var buchung = buchung(Buchungsstatus.STORNIERT, gast(ERWACHSEN), null);

        assertThatThrownBy(() -> buchung.checkeEin(HEUTE, ZIMMER)).isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void gastMitGenau16JahrenKannEinchecken() {
        var buchung = reserviert(gast(HEUTE.minusYears(16)));

        buchung.checkeEin(HEUTE, ZIMMER);

        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.EINGECHECKT);
    }

    @Test
    void gastUnter16KannNichtEinchecken() {
        var buchung = reserviert(gast(HEUTE.minusYears(16).plusDays(1)));

        assertThatThrownBy(() -> buchung.checkeEin(HEUTE, ZIMMER))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("mindestens 16 Jahre");
        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.RESERVIERT);
    }

    @Test
    void checkinOhneGeburtsdatumIstNichtMoeglich() {
        var buchung = reserviert(gast(null));

        assertThatThrownBy(() -> buchung.checkeEin(HEUTE, ZIMMER))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("Geburtsdatum");
    }

    @Test
    void checkinOhneAdresseIstNichtMoeglich() {
        var buchung = reserviert(Gast.neu("Erika", "Mustermann", null, ERWACHSEN));

        assertThatThrownBy(() -> buchung.checkeEin(HEUTE, ZIMMER))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("Adresse");
        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.RESERVIERT);
    }

    @Test
    void reservierungOhneGeburtsdatumUndAdresseIstMoeglich() {
        var buchung = reserviert(Gast.neu(null, "Mustermann", null, null));

        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.RESERVIERT);
    }

    @Test
    void eingecheckteBuchungHatImmerEinZimmer() {
        assertThatThrownBy(() -> buchung(Buchungsstatus.EINGECHECKT, gast(ERWACHSEN), null))
            .isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> buchung(Buchungsstatus.RESERVIERT, gast(ERWACHSEN), ZIMMER))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void reservierteBuchungKannStorniertWerden() {
        var buchung = reserviert(gast(ERWACHSEN));

        buchung.storniere();

        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.STORNIERT);
    }

    @Test
    void eingecheckteBuchungKannNichtStorniertWerden() {
        var buchung = reserviert(gast(ERWACHSEN));
        buchung.checkeEin(HEUTE, ZIMMER);

        assertThatThrownBy(buchung::storniere)
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("eingecheckte Buchung");
        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.EINGECHECKT);
    }

    @Test
    void eingecheckteBuchungWirdAusgechecktUndBehaeltIhrZimmer() {
        var buchung = eingecheckt();

        buchung.checkeAus();

        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.AUSGECHECKT);
        assertThat(buchung.getZugewiesenesZimmer()).isEqualTo(ZIMMER);
    }

    @Test
    void nurEingecheckteBuchungKannAusgechecktWerden() {
        assertThatThrownBy(() -> reserviert(gast(ERWACHSEN)).checkeAus())
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("Nur eine eingecheckte Buchung");
        assertThatThrownBy(() -> buchung(Buchungsstatus.STORNIERT, gast(ERWACHSEN), null).checkeAus())
            .isInstanceOf(DomainAssertionException.class);
        var ausgecheckt = eingecheckt();
        ausgecheckt.checkeAus();
        assertThatThrownBy(ausgecheckt::checkeAus).isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void ausgecheckteBuchungKannWederEingechecktNochStorniertWerden() {
        var buchung = eingecheckt();
        buchung.checkeAus();

        assertThatThrownBy(() -> buchung.checkeEin(HEUTE, ZIMMER))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("bereits eingecheckt");
        assertThatThrownBy(buchung::storniere).isInstanceOf(DomainAssertionException.class);
        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.AUSGECHECKT);
    }

    @Test
    void ausgecheckteBuchungHatImmerEinZimmer() {
        assertThatThrownBy(() -> buchung(Buchungsstatus.AUSGECHECKT, gast(ERWACHSEN), null))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void buchungOhneGastIstUngueltig() {
        assertThatThrownBy(() -> Buchung.walkIn(null, zeitraum(), Zimmerkategorie.SUITE))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void buchungOhneBuchungszeitraumIstUngueltig() {
        assertThatThrownBy(() -> Buchung.walkIn(gast(ERWACHSEN), null, Zimmerkategorie.SUITE))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void buchungOhneZimmerkategorieIstUngueltig() {
        assertThatThrownBy(() -> Buchung.walkIn(gast(ERWACHSEN), zeitraum(), null))
            .isInstanceOf(DomainAssertionException.class);
    }

    private static Buchungszeitraum zeitraum() {
        return new Buchungszeitraum(HEUTE, HEUTE.plusDays(2));
    }

    private static Gast gast(LocalDate geburtsdatum) {
        return Gast.neu("Erika", "Mustermann", new Adresse("Hauptstrasse 1", "76131", "Karlsruhe", "Deutschland"), geburtsdatum);
    }

    private static Buchung eingecheckt() {
        var buchung = reserviert(gast(ERWACHSEN));
        buchung.checkeEin(HEUTE, ZIMMER);
        return buchung;
    }

    private static Buchung reserviert(Gast gast) {
        return buchung(Buchungsstatus.RESERVIERT, gast, null);
    }

    private static Buchung buchung(Buchungsstatus status, Gast gast, Zimmer.ZimmerId zimmer) {
        return Buchung.builder()
            .id(new Buchung.BuchungId(UUID.randomUUID()))
            .gast(gast)
            .buchungszeitraum(new Buchungszeitraum(HEUTE, HEUTE.plusDays(3)))
            .gewuenschteZimmerkategorie(Zimmerkategorie.DOPPELZIMMER)
            .status(status)
            .zugewiesenesZimmer(zimmer)
            .build();
    }
}
