package com.esentri.hotel.rezeption.inbound;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CheckInControllerIntegrationTest {

    private static final String HOTEL = "11111111-1111-1111-1111-111111111111";

    private static final String GASTDATEN = """
        {
          "name": { "vorname": "Max", "nachname": "Mustermann" },
          "email": "max@example.com",
          "adresse": { "strasse": "Ringstrasse 5", "postleitzahl": "10115", "ort": "Berlin", "land": "Deutschland" }
        }
        """;

    @Autowired
    MockMvc mockMvc;

    @Test
    void checkInMitZimmernummer() throws Exception {
        legeZimmerAn("WEB-201").andExpect(status().isCreated());
        var buchungsId = uebernimmReservierung("WEB-CI-1");

        checkeEin(buchungsId, "WEB-201").andExpect(status().isNoContent());
        checkeEin(buchungsId, "WEB-201").andExpect(status().isBadRequest());
    }

    @Test
    void unbekannteZimmernummerLiefertNotFound() throws Exception {
        var buchungsId = uebernimmReservierung("WEB-CI-2");

        checkeEin(buchungsId, "999").andExpect(status().isNotFound());
    }

    @Test
    void doppeltesZimmerLiefertKonflikt() throws Exception {
        legeZimmerAn("WEB-202").andExpect(status().isCreated());
        legeZimmerAn("WEB-202").andExpect(status().isConflict());
    }

    @Test
    void checkInBeiGeplanterWartungImAufenthalt() throws Exception {
        legeZimmerAn("WEB-204").andExpect(status().isCreated());
        var buchungsId = uebernimmReservierung("WEB-CI-3");

        var antwort = mockMvc.perform(post("/api/hotels/{hotelId}/zimmer/{zimmernummer}/wartungen", HOTEL, "WEB-204")
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {
                      "wartungsgrund": "Heizung defekt",
                      "zeitraum": { "von": "2026-11-03", "bis": "2026-11-04" }
                    }
                    """))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        String belegungId = JsonPath.read(antwort, "$.belegungId");
        checkeEin(buchungsId, "WEB-204").andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/hotels/{hotelId}/zimmer/{zimmernummer}/wartungen/{belegungId}/beenden",
                HOTEL, "WEB-204", belegungId))
            .andExpect(status().isNoContent());
        checkeEin(buchungsId, "WEB-204").andExpect(status().isNoContent());
    }

    @Test
    void checkoutUeberZimmernummer() throws Exception {
        legeZimmerAn("WEB-205").andExpect(status().isCreated());
        var buchungsId = uebernimmReservierung("WEB-CI-4");
        checkeEin(buchungsId, "WEB-205").andExpect(status().isNoContent());

        mockMvc.perform(post("/api/hotels/{hotelId}/zimmer/{zimmernummer}/checkout", HOTEL, "WEB-205"))
            .andExpect(status().isNoContent());
        mockMvc.perform(post("/api/hotels/{hotelId}/zimmer/{zimmernummer}/checkout", HOTEL, "WEB-205"))
            .andExpect(status().isNotFound());

        // das Zimmer wird per Event freigegeben: danach kann ein neuer Gast spontan einchecken
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
            mockMvc.perform(post("/api/buchungen/spontan-checkin").contentType(MediaType.APPLICATION_JSON).content("""
                    {
                      "hotelId": "%s",
                      "zimmernummer": "WEB-205",
                      "gastdaten": %s,
                      "aufenthaltszeitraum": { "anreise": "2026-11-02", "abreise": "2026-11-03" }
                    }
                    """.formatted(HOTEL, GASTDATEN)))
                .andExpect(status().isCreated()));
    }

    @Test
    void spontanerCheckIn() throws Exception {
        legeZimmerAn("WEB-203").andExpect(status().isCreated());

        mockMvc.perform(post("/api/buchungen/spontan-checkin").contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "hotelId": "%s",
                  "zimmernummer": "WEB-203",
                  "gastdaten": %s,
                  "aufenthaltszeitraum": { "anreise": "2026-11-02", "abreise": "2026-11-04" },
              "zimmerkategorie": "DOPPELZIMMER"
                }
                """.formatted(HOTEL, GASTDATEN)))
            .andExpect(status().isCreated());
    }

    private org.springframework.test.web.servlet.ResultActions legeZimmerAn(String zimmernummer) throws Exception {
        return mockMvc.perform(post("/api/zimmer").contentType(MediaType.APPLICATION_JSON)
            .content("""
                { "hotelId": "%s", "zimmernummer": "%s", "kategorie": "DOPPELZIMMER" }
                """.formatted(HOTEL, zimmernummer)));
    }

    private String uebernimmReservierung(String reservierungsnummer) throws Exception {
        var antwort = mockMvc.perform(post("/api/buchungen").contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "hotelId": "%s",
                  "reservierungsnummer": "%s",
                  "name": { "vorname": "Max", "nachname": "Mustermann" },
                  "aufenthaltszeitraum": { "anreise": "2026-11-02", "abreise": "2026-11-05" },
              "zimmerkategorie": "DOPPELZIMMER"
                }
                """.formatted(HOTEL, reservierungsnummer)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return JsonPath.read(antwort, "$.buchungsId");
    }

    private org.springframework.test.web.servlet.ResultActions checkeEin(String buchungsId, String zimmernummer)
        throws Exception {
        return mockMvc.perform(post("/api/buchungen/{buchungsId}/checkin", buchungsId)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                { "zimmernummer": "%s", "gastdaten": %s }
                """.formatted(zimmernummer, GASTDATEN)));
    }
}
