package com.esentri.hotel.rezeption;

import com.jayway.jsonpath.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Hilfsaufrufe der REST-Schnittstelle der Rezeption fuer Integrationstests.
 */
record RezeptionApi(MockMvc mockMvc) {

    static final String ADRESSE_JSON =
        "{\"strasse\": \"Hauptstrasse 1\", \"postleitzahl\": \"76131\", \"ort\": \"Karlsruhe\", \"land\": \"Deutschland\"}";

    static String neueZimmernummer() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    UUID legeZimmerAn(String zimmernummer, String zimmerkategorie) throws Exception {
        var antwort = legeZimmerAnRequest(zimmernummer, zimmerkategorie)
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return UUID.fromString(JsonPath.read(antwort, "$.zimmerId"));
    }

    ResultActions legeZimmerAnRequest(String zimmernummer, String zimmerkategorie) throws Exception {
        return mockMvc.perform(post("/api/rezeption/zimmer")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"zimmernummer": "%s", "zimmerkategorie": "%s", "etage": 2, "blickrichtung": "PARK",
                 "balkon": true, "barrierefrei": false, "bettart": "DOPPELBETT", "raucherzimmer": false}
                """.formatted(zimmernummer, zimmerkategorie)));
    }

    ResultActions checkeWalkInEin(String gastJson, String gewuenschteZimmerkategorie, UUID zimmerId) throws Exception {
        return mockMvc.perform(post("/api/rezeption/walk-ins")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {%s, "abreise": "%s", "gewuenschteZimmerkategorie": "%s", "zimmerId": "%s"}
                """.formatted(gastJson, LocalDate.now().plusDays(2), gewuenschteZimmerkategorie, zimmerId)));
    }

    static String gastJson() {
        return """
            "vorname": "Max", "nachname": "Mustermann", "adresse": %s, "geburtsdatum": "1990-03-12"\
            """.formatted(ADRESSE_JSON);
    }
}
