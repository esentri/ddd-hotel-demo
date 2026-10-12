package com.esentri.hotel.rezeption.inbound;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BuchungControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void legtBuchungAusReservierungAn() throws Exception {
        mockMvc.perform(post("/api/buchungen").contentType(MediaType.APPLICATION_JSON).content(reservierung("WEB-1")))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.buchungsId").isString());
    }

    @Test
    void doppelteReservierungLiefertKonflikt() throws Exception {
        mockMvc.perform(post("/api/buchungen").contentType(MediaType.APPLICATION_JSON).content(reservierung("WEB-2")))
            .andExpect(status().isCreated());
        mockMvc.perform(post("/api/buchungen").contentType(MediaType.APPLICATION_JSON).content(reservierung("WEB-2")))
            .andExpect(status().isConflict());
    }

    @Test
    void stornierung() throws Exception {
        var antwort = mockMvc.perform(post("/api/buchungen").contentType(MediaType.APPLICATION_JSON)
                .content(reservierung("WEB-5")))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        String buchungsId = JsonPath.read(antwort, "$.buchungsId");

        mockMvc.perform(post("/api/buchungen/{buchungsId}/stornierung", buchungsId))
            .andExpect(status().isNoContent());
        mockMvc.perform(post("/api/buchungen/{buchungsId}/stornierung", buchungsId))
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/buchungen/{buchungsId}/stornierung", "00000000-0000-0000-0000-000000000000"))
            .andExpect(status().isNotFound());
    }

    @Test
    void ungueltigerZeitraumWirdAbgelehnt() throws Exception {
        var json = """
            {
              "hotelId": "11111111-1111-1111-1111-111111111111",
              "reservierungsnummer": "WEB-3",
              "name": { "vorname": "Erika", "nachname": "Mustermann" },
              "aufenthaltszeitraum": { "anreise": "2026-11-05", "abreise": "2026-11-02" },
              "zimmerkategorie": "DOPPELZIMMER"
            }
            """;
        mockMvc.perform(post("/api/buchungen").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void ungueltigeEmailWirdAbgelehnt() throws Exception {
        var json = """
            {
              "hotelId": "11111111-1111-1111-1111-111111111111",
              "reservierungsnummer": "WEB-4",
              "name": { "vorname": "Erika", "nachname": "Mustermann" },
              "email": "keine-email",
              "aufenthaltszeitraum": { "anreise": "2026-11-02", "abreise": "2026-11-05" },
              "zimmerkategorie": "DOPPELZIMMER"
            }
            """;
        mockMvc.perform(post("/api/buchungen").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isBadRequest());
    }

    private static String reservierung(String reservierungsnummer) {
        return """
            {
              "hotelId": "11111111-1111-1111-1111-111111111111",
              "reservierungsnummer": "%s",
              "name": { "vorname": "Erika", "nachname": "Mustermann" },
              "email": "erika@example.com",
              "aufenthaltszeitraum": { "anreise": "2026-11-02", "abreise": "2026-11-05" },
              "zimmerkategorie": "DOPPELZIMMER"
            }
            """.formatted(reservierungsnummer);
    }
}
