package com.esentri.hotel.rezeption;

import com.esentri.hotel.rezeption.core.domain.zimmer.Bettart;
import com.esentri.hotel.rezeption.core.domain.zimmer.Blickrichtung;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.ZimmerRepository;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerstatus;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.esentri.hotel.rezeption.RezeptionApi.gastJson;
import static com.esentri.hotel.rezeption.RezeptionApi.neueZimmernummer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ZimmerIntegrationTest {

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
    void zimmerWirdBezugsfertigAngelegt() throws Exception {
        var zimmernummer = neueZimmernummer();

        var zimmerId = api.legeZimmerAn(zimmernummer, "DOPPELZIMMER");

        var zimmer = zimmerRepository.findById(new Zimmer.ZimmerId(zimmerId)).orElseThrow();
        assertThat(zimmer.getZimmernummer()).isEqualTo(new Zimmernummer(zimmernummer));
        assertThat(zimmer.getZimmerkategorie()).isEqualTo(Zimmerkategorie.DOPPELZIMMER);
        assertThat(zimmer.getEtage()).isEqualTo(2);
        assertThat(zimmer.getBlickrichtung()).isEqualTo(Blickrichtung.PARK);
        assertThat(zimmer.isBalkon()).isTrue();
        assertThat(zimmer.isBarrierefrei()).isFalse();
        assertThat(zimmer.getBettart()).isEqualTo(Bettart.DOPPELBETT);
        assertThat(zimmer.isRaucherzimmer()).isFalse();
        assertThat(zimmer.getStatus()).isEqualTo(Zimmerstatus.BEZUGSFERTIG);
        assertThat(zimmerRepository.findByZimmernummer(new Zimmernummer(zimmernummer))).contains(zimmer);
    }

    @Test
    void zimmernummerIstEindeutig() throws Exception {
        var zimmernummer = neueZimmernummer();
        api.legeZimmerAn(zimmernummer, "SUITE");

        api.legeZimmerAnRequest(zimmernummer, "EINZELZIMMER")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Ein Zimmer mit der Nummer " + zimmernummer + " gibt es bereits."));
    }

    @Test
    void uebersichtZeigtBezugsfertigeZimmerDerGewuenschtenUndHoeherenKategorien() throws Exception {
        var einzelzimmer = api.legeZimmerAn(neueZimmernummer(), "EINZELZIMMER");
        var doppelzimmerNummer = neueZimmernummer();
        var doppelzimmer = api.legeZimmerAn(doppelzimmerNummer, "DOPPELZIMMER");
        var suite = api.legeZimmerAn(neueZimmernummer(), "SUITE");
        var belegt = api.legeZimmerAn(neueZimmernummer(), "DOPPELZIMMER");
        api.checkeWalkInEin(gastJson(), "DOPPELZIMMER", belegt).andExpect(status().isCreated());

        var antwort = mockMvc.perform(get("/api/rezeption/zimmer/verfuegbar")
                .param("gewuenschteZimmerkategorie", "DOPPELZIMMER"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        List<Map<String, Object>> zimmer = JsonPath.read(antwort, "$");
        var ids = zimmer.stream().map(z -> UUID.fromString((String) z.get("zimmerId"))).toList();
        assertThat(ids).contains(doppelzimmer, suite).doesNotContain(einzelzimmer, belegt);
        assertThat(ids.indexOf(doppelzimmer)).isLessThan(ids.indexOf(suite));

        var doppelzimmerEintrag = zimmer.get(ids.indexOf(doppelzimmer));
        assertThat(doppelzimmerEintrag)
            .containsEntry("zimmernummer", doppelzimmerNummer)
            .containsEntry("zimmerkategorie", "DOPPELZIMMER")
            .containsEntry("upgrade", false)
            .containsEntry("etage", 2)
            .containsEntry("blickrichtung", "PARK")
            .containsEntry("balkon", true)
            .containsEntry("barrierefrei", false)
            .containsEntry("bettart", "DOPPELBETT")
            .containsEntry("raucherzimmer", false);
        assertThat(zimmer.get(ids.indexOf(suite))).containsEntry("upgrade", true);
    }
}
