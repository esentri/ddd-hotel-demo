package com.esentri.hotel.rezeption.core.domain.buchung;

import io.domainlifecycles.assertion.DomainAssertionException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BuchungTest {

    private static final LocalDate HEUTE = LocalDate.of(2026, 10, 15);

    @Test
    void walkInLegtEingecheckteBuchungAn() {
        var buchung = Buchung.walkIn(gast(), new Buchungszeitraum(HEUTE, HEUTE.plusDays(2)), Zimmerkategorie.SUITE);

        assertThat(buchung.getId()).isNotNull();
        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.EINGECHECKT);
        assertThat(buchung.getGewuenschteZimmerkategorie()).isEqualTo(Zimmerkategorie.SUITE);
    }

    @Test
    void angelegteBuchungKannEingechecktWerden() {
        var buchung = buchung(Buchungsstatus.ANGELEGT);

        buchung.checkeEin();

        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.EINGECHECKT);
    }

    @Test
    void buchungKannNurEinmalEingechecktWerden() {
        var buchung = Buchung.walkIn(gast(), new Buchungszeitraum(HEUTE, HEUTE.plusDays(1)), Zimmerkategorie.EINZELZIMMER);

        assertThatThrownBy(buchung::checkeEin)
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("bereits eingecheckt");
    }

    @Test
    void stornierteBuchungKannNichtEingechecktWerden() {
        var buchung = buchung(Buchungsstatus.STORNIERT);

        assertThatThrownBy(buchung::checkeEin).isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void nichtEingecheckteBuchungKannStorniertWerden() {
        var buchung = buchung(Buchungsstatus.ANGELEGT);

        buchung.storniere();

        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.STORNIERT);
    }

    @Test
    void eingecheckteBuchungKannNichtStorniertWerden() {
        var buchung = Buchung.walkIn(gast(), new Buchungszeitraum(HEUTE, HEUTE.plusDays(1)), Zimmerkategorie.DOPPELZIMMER);

        assertThatThrownBy(buchung::storniere)
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("eingecheckte Buchung");
        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.EINGECHECKT);
    }

    @Test
    void buchungOhneGastIstUngueltig() {
        assertThatThrownBy(() -> Buchung.walkIn(null, new Buchungszeitraum(HEUTE, HEUTE.plusDays(1)), Zimmerkategorie.SUITE))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void buchungOhneBuchungszeitraumIstUngueltig() {
        assertThatThrownBy(() -> Buchung.walkIn(gast(), null, Zimmerkategorie.SUITE))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void buchungOhneZimmerkategorieIstUngueltig() {
        assertThatThrownBy(() -> Buchung.walkIn(gast(), new Buchungszeitraum(HEUTE, HEUTE.plusDays(1)), null))
            .isInstanceOf(DomainAssertionException.class);
    }

    private static Gast gast() {
        return Gast.neu("Erika", "Mustermann");
    }

    private static Buchung buchung(Buchungsstatus status) {
        return Buchung.builder()
            .id(new Buchung.BuchungId(UUID.randomUUID()))
            .gast(gast())
            .buchungszeitraum(new Buchungszeitraum(HEUTE, HEUTE.plusDays(3)))
            .gewuenschteZimmerkategorie(Zimmerkategorie.DOPPELZIMMER)
            .status(status)
            .build();
    }
}
