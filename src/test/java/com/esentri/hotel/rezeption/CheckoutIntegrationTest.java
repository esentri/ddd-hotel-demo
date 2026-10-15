package com.esentri.hotel.rezeption;

import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.buchung.BuchungRepository;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchungsstatus;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.ZimmerRepository;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerstatus;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Duration;
import java.util.UUID;

import static com.esentri.hotel.rezeption.RezeptionApi.gastJson;
import static com.esentri.hotel.rezeption.RezeptionApi.neueZimmernummer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CheckoutIntegrationTest {

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
    void checkoutChecktBuchungAusUndZimmerIstZuReinigen() throws Exception {
        var zimmerId = api.legeZimmerAn(neueZimmernummer(), "DOPPELZIMMER");
        var buchungId = walkIn(zimmerId);

        checkeAus(buchungId).andExpect(status().isNoContent());

        var buchung = buchungRepository.findById(new Buchung.BuchungId(buchungId)).orElseThrow();
        assertThat(buchung.getStatus()).isEqualTo(Buchungsstatus.AUSGECHECKT);
        assertThat(buchung.getZugewiesenesZimmer()).isEqualTo(new Zimmer.ZimmerId(zimmerId));
        warteBisZimmerZuReinigenIst(zimmerId);
        assertThat(zimmerRepository.findById(new Zimmer.ZimmerId(zimmerId)).orElseThrow().getBelegtDurch()).isNull();
    }

    @Test
    void zuReinigendesZimmerIstNichtVerfuegbar() throws Exception {
        var zimmerId = api.legeZimmerAn(neueZimmernummer(), "DOPPELZIMMER");
        checkeAus(walkIn(zimmerId)).andExpect(status().isNoContent());
        warteBisZimmerZuReinigenIst(zimmerId);

        var antwort = mockMvc.perform(get("/api/rezeption/zimmer/verfuegbar")
                .param("gewuenschteZimmerkategorie", "DOPPELZIMMER"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        assertThat(JsonPath.<java.util.List<String>>read(antwort, "$[*].zimmerId")).doesNotContain(zimmerId.toString());
    }

    @Test
    void buchungKannNurEinmalAusgechecktWerden() throws Exception {
        var buchungId = walkIn(api.legeZimmerAn(neueZimmernummer(), "SUITE"));
        checkeAus(buchungId).andExpect(status().isNoContent());

        checkeAus(buchungId)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Nur eine eingecheckte Buchung kann ausgecheckt werden."));
    }

    @Test
    void unbekannteBuchungWirdAbgelehnt() throws Exception {
        checkeAus(UUID.randomUUID())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Die Buchung ist nicht bekannt."));
    }

    /**
     * Das Zimmer wird entkoppelt nach dem Commit des Check-outs geraeumt (Domain Event).
     */
    private void warteBisZimmerZuReinigenIst(UUID zimmerId) {
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
            assertThat(zimmerRepository.findById(new Zimmer.ZimmerId(zimmerId)).orElseThrow().getStatus())
                .isEqualTo(Zimmerstatus.ZU_REINIGEN));
    }

    private UUID walkIn(UUID zimmerId) throws Exception {
        var antwort = api.checkeWalkInEin(gastJson(), "EINZELZIMMER", zimmerId)
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return UUID.fromString(JsonPath.read(antwort, "$.buchungId"));
    }

    private ResultActions checkeAus(UUID buchungId) throws Exception {
        return mockMvc.perform(post("/api/rezeption/buchungen/{buchungId}/checkout", buchungId));
    }
}
