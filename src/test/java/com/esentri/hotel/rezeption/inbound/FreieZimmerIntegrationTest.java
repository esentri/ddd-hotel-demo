package com.esentri.hotel.rezeption.inbound;

import com.esentri.hotel.rezeption.core.application.BuchungApplicationService;
import com.esentri.hotel.rezeption.core.application.ZimmerApplicationService;
import com.esentri.hotel.rezeption.core.domain.buchung.Aufenthaltszeitraum;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeEin;
import com.esentri.hotel.rezeption.core.domain.buchung.CheckeSpontanEin;
import com.esentri.hotel.rezeption.core.domain.buchung.Name;
import com.esentri.hotel.rezeption.core.domain.buchung.Reservierungsnummer;
import com.esentri.hotel.rezeption.core.domain.buchung.StorniereBuchung;
import com.esentri.hotel.rezeption.core.domain.buchung.UebernimmReservierung;
import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import com.esentri.hotel.rezeption.core.domain.verfuegbarkeit.FreieZimmerAmTag;
import com.esentri.hotel.rezeption.core.domain.verfuegbarkeit.VerfuegbarkeitJeKategorie;
import com.esentri.hotel.rezeption.core.domain.zimmer.Belegungszeitraum;
import com.esentri.hotel.rezeption.core.domain.zimmer.LegeZimmerAn;
import com.esentri.hotel.rezeption.core.domain.zimmer.PlaneWartung;
import com.esentri.hotel.rezeption.core.domain.zimmer.Wartungsgrund;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.ANREISE;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.ZEITRAUM;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.gastdaten;
import static com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie.DOPPELZIMMER;
import static com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie.EINZELZIMMER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Hotel mit den Doppelzimmern 101, 102 und den Einzelzimmern 103, 104. Heute ist der 02.11.:
 * 101 ist durch einen Gast vom 02. bis 05.11. belegt, 102 wird vom 04. bis 06.11. gewartet.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FreieZimmerIntegrationTest {

    @Autowired
    ZimmerApplicationService zimmerApplicationService;

    @Autowired
    BuchungApplicationService buchungApplicationService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void freieZimmerJeKategorieDerNaechstenZweiWochen() {
        var hotel = hotelMitBelegungen();

        var uebersicht = zimmerApplicationService.freieZimmerDerNaechstenZweiWochen(hotel);

        assertThat(uebersicht.anzahlZimmer()).isEqualTo(4);
        assertThat(uebersicht.von()).isEqualTo(ANREISE);
        assertThat(uebersicht.bis()).isEqualTo(ANREISE.plusDays(14));
        assertThat(uebersicht.tage()).hasSize(14);
        var tage = uebersicht.tage();
        assertThat(freieZimmer(tage.get(0), DOPPELZIMMER)).containsExactly("102");
        assertThat(freieZimmer(tage.get(2), DOPPELZIMMER)).isEmpty();
        assertThat(freieZimmer(tage.get(3), DOPPELZIMMER)).containsExactly("101");
        assertThat(freieZimmer(tage.get(4), DOPPELZIMMER)).containsExactly("101", "102");
        assertThat(freieZimmer(tage.get(0), EINZELZIMMER)).containsExactly("103", "104");
    }

    /**
     * Offene Reservierungen: R1 Doppel 02.-05.11., R2 Einzel 03.-04.11., R3 Doppel 04.-05.11.;
     * R4 Einzel 02.-03.11. ist storniert und zaehlt nicht.
     */
    @Test
    void offeneReservierungenWerdenJeKategorieGegenFreieZimmerGerechnet() {
        var hotel = hotelMitBelegungen();
        reserviere(hotel, "FZ-R1", DOPPELZIMMER, 2, 5);
        reserviere(hotel, "FZ-R2", EINZELZIMMER, 3, 4);
        reserviere(hotel, "FZ-R3", DOPPELZIMMER, 4, 5);
        var storniert = reserviere(hotel, "FZ-R4", EINZELZIMMER, 2, 3);
        buchungApplicationService.storniere(new StorniereBuchung(storniert));

        var tage = zimmerApplicationService.freieZimmerDerNaechstenZweiWochen(hotel).tage();

        // Kategorie, frei, offen, noch verfuegbar
        assertKategorie(tage.get(0), DOPPELZIMMER, 1, 1, 0);
        assertKategorie(tage.get(0), EINZELZIMMER, 2, 0, 2);
        assertKategorie(tage.get(1), DOPPELZIMMER, 1, 1, 0);
        assertKategorie(tage.get(1), EINZELZIMMER, 2, 1, 1);
        assertKategorie(tage.get(2), DOPPELZIMMER, 0, 2, -2);
        assertKategorie(tage.get(2), EINZELZIMMER, 2, 0, 2);
        assertKategorie(tage.get(3), DOPPELZIMMER, 1, 0, 1);
    }

    @Test
    void eingecheckteReservierungZaehltNichtAlsOffen() {
        var hotel = hotelMitBelegungen();
        zimmerApplicationService.legeZimmerAn(new LegeZimmerAn(hotel, new Zimmernummer("105"), DOPPELZIMMER));
        var buchungsId = reserviere(hotel, "FZ-R5", DOPPELZIMMER, 2, 5);
        buchungApplicationService.checkeEin(new CheckeEin(buchungsId, new Zimmernummer("105"), gastdaten()));

        var heute = zimmerApplicationService.freieZimmerDerNaechstenZweiWochen(hotel).tage().get(0);

        assertKategorie(heute, DOPPELZIMMER, 1, 0, 1);
    }

    @Test
    void freieZimmerUeberRest() throws Exception {
        var hotel = hotelMitBelegungen();

        mockMvc.perform(get("/api/hotels/{hotelId}/freie-zimmer", hotel.value()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.anzahlZimmer").value(4))
            .andExpect(jsonPath("$.tage.length()").value(14))
            .andExpect(jsonPath("$.tage[0].datum").value("2026-11-02"))
            .andExpect(jsonPath("$.tage[0].kategorien[?(@.kategorie == 'EINZELZIMMER')].freieZimmer[*]",
                contains("103", "104")))
            .andExpect(jsonPath("$.tage[2].kategorien[?(@.kategorie == 'DOPPELZIMMER')].nochVerfuegbar",
                contains(0)));
    }

    private HotelId hotelMitBelegungen() {
        var hotel = new HotelId(UUID.randomUUID());
        legeZimmerAn(hotel, "101", DOPPELZIMMER);
        legeZimmerAn(hotel, "102", DOPPELZIMMER);
        legeZimmerAn(hotel, "103", EINZELZIMMER);
        legeZimmerAn(hotel, "104", EINZELZIMMER);
        buchungApplicationService.checkeSpontanEin(
            new CheckeSpontanEin(hotel, new Zimmernummer("101"), gastdaten(), ZEITRAUM));
        zimmerApplicationService.planeWartung(new PlaneWartung(hotel, new Zimmernummer("102"),
            new Wartungsgrund("Fenster"), new Belegungszeitraum(LocalDate.of(2026, 11, 4), LocalDate.of(2026, 11, 6))));
        return hotel;
    }

    private void legeZimmerAn(HotelId hotel, String nummer, Zimmerkategorie kategorie) {
        zimmerApplicationService.legeZimmerAn(new LegeZimmerAn(hotel, new Zimmernummer(nummer), kategorie));
    }

    private Buchung.BuchungsId reserviere(HotelId hotel, String reservierungsnummer, Zimmerkategorie kategorie,
                                          int anreiseTag, int abreiseTag) {
        return buchungApplicationService.uebernimmReservierung(new UebernimmReservierung(hotel,
            new Reservierungsnummer(reservierungsnummer), new Name("Erika", "Mustermann"), null, null, null,
            new Aufenthaltszeitraum(LocalDate.of(2026, 11, anreiseTag), LocalDate.of(2026, 11, abreiseTag)),
            kategorie));
    }

    private static VerfuegbarkeitJeKategorie kategorie(FreieZimmerAmTag tag, Zimmerkategorie kategorie) {
        return tag.kategorien().stream().filter(k -> k.kategorie() == kategorie).findFirst().orElseThrow();
    }

    private static List<String> freieZimmer(FreieZimmerAmTag tag, Zimmerkategorie kategorie) {
        return kategorie(tag, kategorie).freieZimmer().stream().map(Zimmernummer::value).toList();
    }

    private static void assertKategorie(FreieZimmerAmTag tag, Zimmerkategorie kategorie, int frei, int offen,
                                        int nochVerfuegbar) {
        var verfuegbarkeit = kategorie(tag, kategorie);
        var beschreibung = "%s am %s".formatted(kategorie, tag.datum());
        assertThat(verfuegbarkeit.anzahlFreieZimmer()).as("frei: " + beschreibung).isEqualTo(frei);
        assertThat(verfuegbarkeit.offeneReservierungen()).as("offen: " + beschreibung).isEqualTo(offen);
        assertThat(verfuegbarkeit.nochVerfuegbar()).as("noch verfuegbar: " + beschreibung).isEqualTo(nochVerfuegbar);
    }
}
