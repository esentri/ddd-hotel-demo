package com.esentri.hotel.rezeption;

import com.esentri.hotel.rezeption.core.domain.buchung.Adresse;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungRepository;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchungsstatus;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchungszeitraum;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.ZimmerRepository;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerstatus;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static com.esentri.hotel.rezeption.RezeptionApi.ADRESSE_JSON;
import static com.esentri.hotel.rezeption.RezeptionApi.gastJson;
import static com.esentri.hotel.rezeption.RezeptionApi.neueZimmernummer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WalkInIntegrationTest {

    @Autowired
    private BuchungRepository buchungRepository;

    @Autowired
    private ZimmerRepository zimmerRepository;

    @Autowired
    private MockMvc mockMvc;

    private RezeptionApi api;

    @BeforeEach
    void setUp() {
        api = new RezeptionApi(mockMvc);
    }

    @Test
    void walkInWeistDemGastDasAusgewaehlteZimmerZu() throws Exception {
        var zimmerId = api.legeZimmerAn(neueZimmernummer(), "SUITE");

        var antwort = api.checkeWalkInEin(gastJson(), "SUITE", zimmerId)
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        var buchungId = new Buchung.BuchungId(UUID.fromString(JsonPath.read(antwort, "$.buchungId")));
        var buchung = buchungRepository.findById(buchungId).orElseThrow();
        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.EINGECHECKT);
        assertThat(buchung.getZugewiesenesZimmer()).isEqualTo(new Zimmer.ZimmerId(zimmerId));
        assertThat(buchung.getBuchungszeitraum()).isEqualTo(new Buchungszeitraum(LocalDate.now(), LocalDate.now().plusDays(2)));
        assertThat(buchung.getGewuenschteZimmerkategorie()).isEqualTo(Zimmerkategorie.SUITE);
        assertThat(buchung.getGast().getAdresse())
            .isEqualTo(new Adresse("Hauptstrasse 1", "76131", "Karlsruhe", "Deutschland"));
        assertThat(buchung.getGast().getGeburtsdatum()).isEqualTo(LocalDate.of(1990, 3, 12));

        var zimmer = zimmerRepository.findById(new Zimmer.ZimmerId(zimmerId)).orElseThrow();
        assertThat(zimmer.getStatus()).isEqualTo(Zimmerstatus.BELEGT);
        assertThat(zimmer.getBelegtDurch()).isEqualTo(buchungId);
    }

    @Test
    void walkInMitUpgrade() throws Exception {
        var suite = api.legeZimmerAn(neueZimmernummer(), "SUITE");

        api.checkeWalkInEin(gastJson(), "EINZELZIMMER", suite).andExpect(status().isCreated());
    }

    @Test
    void zimmerNiedrigererKategorieWirdAbgelehnt() throws Exception {
        var einzelzimmer = api.legeZimmerAn(neueZimmernummer(), "EINZELZIMMER");

        api.checkeWalkInEin(gastJson(), "SUITE", einzelzimmer)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("Zimmerkategorie")));
        assertThat(zimmerRepository.findById(new Zimmer.ZimmerId(einzelzimmer)).orElseThrow().getStatus())
            .isEqualTo(Zimmerstatus.BEZUGSFERTIG);
    }

    @Test
    void belegtesZimmerKannNichtNochmalZugewiesenWerden() throws Exception {
        var zimmerId = api.legeZimmerAn(neueZimmernummer(), "DOPPELZIMMER");
        api.checkeWalkInEin(gastJson(), "DOPPELZIMMER", zimmerId).andExpect(status().isCreated());

        api.checkeWalkInEin(gastJson(), "DOPPELZIMMER", zimmerId)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("nicht bezugsfertig")));
    }

    @Test
    void unbekanntesZimmerWirdAbgelehnt() throws Exception {
        api.checkeWalkInEin(gastJson(), "SUITE", UUID.randomUUID())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Das Zimmer ist nicht bekannt."));
    }

    @Test
    void walkInUnter16WirdAbgelehntUndZimmerBleibtFrei() throws Exception {
        var zimmerId = api.legeZimmerAn(neueZimmernummer(), "SUITE");
        var gast = """
            "nachname": "Mustermann", "adresse": %s, "geburtsdatum": "%s"\
            """.formatted(ADRESSE_JSON, LocalDate.now().minusYears(15));

        api.checkeWalkInEin(gast, "SUITE", zimmerId)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Der Gast muss zum Check-in mindestens 16 Jahre alt sein."));
        assertThat(zimmerRepository.findById(new Zimmer.ZimmerId(zimmerId)).orElseThrow().getStatus())
            .isEqualTo(Zimmerstatus.BEZUGSFERTIG);
    }

    @Test
    void walkInOhneGeburtsdatumWirdAbgelehnt() throws Exception {
        var zimmerId = api.legeZimmerAn(neueZimmernummer(), "SUITE");

        api.checkeWalkInEin("\"nachname\": \"Mustermann\", \"adresse\": " + ADRESSE_JSON, "SUITE", zimmerId)
            .andExpect(status().isBadRequest());
    }

    @Test
    void walkInOhneAdresseWirdAbgelehnt() throws Exception {
        var zimmerId = api.legeZimmerAn(neueZimmernummer(), "SUITE");

        api.checkeWalkInEin("\"nachname\": \"Mustermann\", \"geburtsdatum\": \"1990-03-12\"", "SUITE", zimmerId)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Fuer den Check-in wird die Adresse des Gastes benoetigt."));
    }

    @Test
    void walkInOhneNachnameWirdAbgelehnt() throws Exception {
        var zimmerId = api.legeZimmerAn(neueZimmernummer(), "SUITE");

        api.checkeWalkInEin("\"vorname\": \"Max\", \"adresse\": " + ADRESSE_JSON + ", \"geburtsdatum\": \"1990-03-12\"",
                "SUITE", zimmerId)
            .andExpect(status().isBadRequest());
    }
}
