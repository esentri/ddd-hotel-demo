package com.esentri.hotel.rezeption;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WalkInIntegrationTest {

    @Autowired
    private BuchungRepository buchungRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void buchungRoundTrip() {
        var heute = LocalDate.now();
        var buchung = Buchung.walkIn(
            Gast.neu("Erika", "Mustermann"),
            new Buchungszeitraum(heute, heute.plusDays(2)),
            Zimmerkategorie.DOPPELZIMMER);

        buchungRepository.insert(buchung);
        var geladen = buchungRepository.findById(buchung.getId()).orElseThrow();

        assertThat(geladen).usingRecursiveComparison().ignoringFields("concurrencyVersion").isEqualTo(buchung);
        assertThat(geladen.getGast().getNachname()).isEqualTo("Mustermann");

        assertThat(buchungRepository.deleteById(buchung.getId())).isPresent();
        assertThat(buchungRepository.findById(buchung.getId())).isEmpty();
    }

    @Test
    void walkInUeberRest() throws Exception {
        var abreise = LocalDate.now().plusDays(3);
        var antwort = mockMvc.perform(post("/api/rezeption/walk-ins")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"vorname": "Max", "nachname": "Mustermann", "abreise": "%s", "gewuenschteZimmerkategorie": "SUITE"}
                    """.formatted(abreise)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        var buchungId = new Buchung.BuchungId(UUID.fromString(JsonPath.read(antwort, "$.buchungId")));
        var buchung = buchungRepository.findById(buchungId).orElseThrow();
        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.EINGECHECKT);
        assertThat(buchung.getBuchungszeitraum()).isEqualTo(new Buchungszeitraum(LocalDate.now(), abreise));
        assertThat(buchung.getGewuenschteZimmerkategorie()).isEqualTo(Zimmerkategorie.SUITE);
        assertThat(buchung.getGast().getVorname()).isEqualTo("Max");
    }

    @Test
    void walkInOhneNachnameWirdAbgelehnt() throws Exception {
        mockMvc.perform(post("/api/rezeption/walk-ins")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"vorname": "Max", "abreise": "%s", "gewuenschteZimmerkategorie": "SUITE"}
                    """.formatted(LocalDate.now().plusDays(1))))
            .andExpect(status().isBadRequest());
    }

    @Test
    void walkInMitAbreiseHeuteWirdAbgelehnt() throws Exception {
        mockMvc.perform(post("/api/rezeption/walk-ins")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"nachname": "Mustermann", "abreise": "%s", "gewuenschteZimmerkategorie": "SUITE"}
                    """.formatted(LocalDate.now())))
            .andExpect(status().isBadRequest());
    }
}
