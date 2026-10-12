package com.esentri.hotel.rezeption.outbound;

import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungRepository;
import com.esentri.hotel.rezeption.core.domain.buchung.Reservierungsnummer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.reservierung;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.reservierungOhneKontaktdaten;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JdbcBuchungRepositoryIntegrationTest {

    @Autowired
    BuchungRepository buchungRepository;

    @Test
    void roundTripMitHauptgast() {
        var buchung = buchungRepository.insert(Buchung.uebernimmReservierung(reservierung("REPO-1")));

        var geladen = buchungRepository.findById(buchung.id()).orElseThrow();
        assertThat(geladen.getReservierungsnummer()).isEqualTo(buchung.getReservierungsnummer());
        assertThat(geladen.getAufenthaltszeitraum()).isEqualTo(buchung.getAufenthaltszeitraum());
        assertThat(geladen.getStatus()).isEqualTo(buchung.getStatus());
        assertThat(geladen.getZimmerId()).isEmpty();
        var gast = geladen.getHauptgast();
        assertThat(gast.id()).isEqualTo(buchung.getHauptgast().id());
        assertThat(gast.getName()).isEqualTo(buchung.getHauptgast().getName());
        assertThat(gast.getEmail()).isEqualTo(buchung.getHauptgast().getEmail());
        assertThat(gast.getTelefonnummer()).isEqualTo(buchung.getHauptgast().getTelefonnummer());
        assertThat(gast.getAdresse()).isEqualTo(buchung.getHauptgast().getAdresse());

        var version = geladen.concurrencyVersion();
        assertThat(buchungRepository.update(geladen).concurrencyVersion()).isGreaterThanOrEqualTo(version);

        assertThat(buchungRepository.deleteById(buchung.id())).isPresent();
        assertThat(buchungRepository.findById(buchung.id())).isEmpty();
    }

    @Test
    void hauptgastOhneKontaktdatenWirdGespeichert() {
        var buchung = buchungRepository.insert(Buchung.uebernimmReservierung(reservierungOhneKontaktdaten("REPO-2")));

        var gast = buchungRepository.findById(buchung.id()).orElseThrow().getHauptgast();
        assertThat(gast.getEmail()).isEmpty();
        assertThat(gast.getTelefonnummer()).isEmpty();
        assertThat(gast.getAdresse()).isEmpty();
    }

    @Test
    void findetBuchungUeberReservierungsnummer() {
        var buchung = buchungRepository.insert(Buchung.uebernimmReservierung(reservierung("REPO-3")));

        assertThat(buchungRepository.findByReservierungsnummer(new Reservierungsnummer("REPO-3")))
            .map(Buchung::id).contains(buchung.id());
        assertThat(buchungRepository.findByReservierungsnummer(new Reservierungsnummer("UNBEKANNT"))).isEmpty();
    }
}
