package com.esentri.hotel.rezeption;

import com.esentri.hotel.rezeption.core.domain.buchung.Adresse;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungRepository;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchungsstatus;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchungszeitraum;
import com.esentri.hotel.rezeption.core.domain.buchung.Gast;
import com.esentri.hotel.rezeption.core.domain.buchung.Zimmerkategorie;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WalkInIntegrationTest {

    private static final Adresse ADRESSE = new Adresse("Hauptstrasse 1", "76131", "Karlsruhe", "Deutschland");
    private static final String ADRESSE_JSON =
        "{\"strasse\": \"Hauptstrasse 1\", \"postleitzahl\": \"76131\", \"ort\": \"Karlsruhe\", \"land\": \"Deutschland\"}";

    @Autowired
    private BuchungRepository buchungRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void buchungRoundTrip() {
        var heute = LocalDate.now();
        var buchung = Buchung.walkIn(
            Gast.neu("Erika", "Mustermann", ADRESSE, LocalDate.of(1980, 5, 1)),
            new Buchungszeitraum(heute, heute.plusDays(2)),
            Zimmerkategorie.DOPPELZIMMER);

        buchungRepository.insert(buchung);
        var geladen = buchungRepository.findById(buchung.getId()).orElseThrow();

        assertThat(geladen).usingRecursiveComparison().ignoringFields("concurrencyVersion").isEqualTo(buchung);
        assertThat(geladen.getGast().getAdresse()).isEqualTo(ADRESSE);
        assertThat(geladen.getGast().getGeburtsdatum()).isEqualTo(LocalDate.of(1980, 5, 1));

        assertThat(buchungRepository.deleteById(buchung.getId())).isPresent();
        assertThat(buchungRepository.findById(buchung.getId())).isEmpty();
    }

    @Test
    void reservierteBuchungMitUnvollstaendigenGastdatenRoundTrip() {
        var heute = LocalDate.now();
        var buchung = Buchung.builder()
            .id(new Buchung.BuchungId(UUID.randomUUID()))
            .gast(Gast.neu(null, "Mustermann", null, null))
            .buchungszeitraum(new Buchungszeitraum(heute.plusDays(5), heute.plusDays(7)))
            .gewuenschteZimmerkategorie(Zimmerkategorie.EINZELZIMMER)
            .status(Buchungsstatus.RESERVIERT)
            .build();

        buchungRepository.insert(buchung);
        var geladen = buchungRepository.findById(buchung.getId()).orElseThrow();

        assertThat(geladen.getStatus()).isEqualTo(Buchungsstatus.RESERVIERT);
        assertThat(geladen.getGast().getAdresse()).isNull();
        assertThat(geladen.getGast().getGeburtsdatum()).isNull();
    }

    @Test
    void walkInUeberRest() throws Exception {
        var abreise = LocalDate.now().plusDays(3);
        var antwort = mockMvc.perform(post("/api/rezeption/walk-ins")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"vorname": "Max", "nachname": "Mustermann",
                     "adresse": {"strasse": "Hauptstrasse 1", "postleitzahl": "76131", "ort": "Karlsruhe", "land": "Deutschland"},
                     "geburtsdatum": "1990-03-12",
                     "abreise": "%s", "gewuenschteZimmerkategorie": "SUITE"}
                    """.formatted(abreise)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        var buchungId = new Buchung.BuchungId(UUID.fromString(JsonPath.read(antwort, "$.buchungId")));
        var buchung = buchungRepository.findById(buchungId).orElseThrow();
        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.EINGECHECKT);
        assertThat(buchung.getBuchungszeitraum()).isEqualTo(new Buchungszeitraum(LocalDate.now(), abreise));
        assertThat(buchung.getGewuenschteZimmerkategorie()).isEqualTo(Zimmerkategorie.SUITE);
        assertThat(buchung.getGast().getVorname()).isEqualTo("Max");
        assertThat(buchung.getGast().getAdresse()).isEqualTo(ADRESSE);
        assertThat(buchung.getGast().getGeburtsdatum()).isEqualTo(LocalDate.of(1990, 3, 12));
    }

    @Test
    void walkInUnter16WirdAbgelehnt() throws Exception {
        mockMvc.perform(post("/api/rezeption/walk-ins")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"nachname": "Mustermann", "adresse": %s, "geburtsdatum": "%s", "abreise": "%s", "gewuenschteZimmerkategorie": "SUITE"}
                    """.formatted(ADRESSE_JSON, LocalDate.now().minusYears(15), LocalDate.now().plusDays(1))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Der Gast muss zum Check-in mindestens 16 Jahre alt sein."));
    }

    @Test
    void walkInOhneGeburtsdatumWirdAbgelehnt() throws Exception {
        mockMvc.perform(post("/api/rezeption/walk-ins")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"nachname": "Mustermann", "adresse": %s, "abreise": "%s", "gewuenschteZimmerkategorie": "SUITE"}
                    """.formatted(ADRESSE_JSON, LocalDate.now().plusDays(1))))
            .andExpect(status().isBadRequest());
    }

    @Test
    void walkInOhneNachnameWirdAbgelehnt() throws Exception {
        mockMvc.perform(post("/api/rezeption/walk-ins")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"vorname": "Max", "adresse": %s, "geburtsdatum": "1990-03-12", "abreise": "%s", "gewuenschteZimmerkategorie": "SUITE"}
                    """.formatted(ADRESSE_JSON, LocalDate.now().plusDays(1))))
            .andExpect(status().isBadRequest());
    }

    @Test
    void walkInMitAbreiseHeuteWirdAbgelehnt() throws Exception {
        mockMvc.perform(post("/api/rezeption/walk-ins")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"nachname": "Mustermann", "adresse": %s, "geburtsdatum": "1990-03-12", "abreise": "%s", "gewuenschteZimmerkategorie": "SUITE"}
                    """.formatted(ADRESSE_JSON, LocalDate.now())))
            .andExpect(status().isBadRequest());
    }

    @Test
    void walkInOhneAdresseWirdAbgelehnt() throws Exception {
        mockMvc.perform(post("/api/rezeption/walk-ins")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"nachname": "Mustermann", "geburtsdatum": "1990-03-12", "abreise": "%s", "gewuenschteZimmerkategorie": "SUITE"}
                    """.formatted(LocalDate.now().plusDays(1))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Fuer den Check-in wird die Adresse des Gastes benoetigt."));
    }
}
