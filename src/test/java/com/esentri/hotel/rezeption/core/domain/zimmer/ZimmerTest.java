package com.esentri.hotel.rezeption.core.domain.zimmer;

import com.esentri.hotel.rezeption.core.domain.DomainTestSupport;
import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import io.domainlifecycles.assertion.DomainAssertionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.ANDERES_HOTEL;
import static com.esentri.hotel.rezeption.core.domain.buchung.BuchungTestdaten.HOTEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ZimmerTest {

    private static final LocalDate HEUTE = LocalDate.of(2026, 11, 2);
    private static final Wartungsgrund WASSERSCHADEN = new Wartungsgrund("Wasserschaden im Bad");

    @BeforeAll
    static void initDomain() {
        DomainTestSupport.initDomain();
    }

    // --- Belegungszeitraum

    @Test
    void belegungszeitraumBeginntVorSeinemEnde() {
        assertThatThrownBy(() -> zeitraum(2, 2)).isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> zeitraum(3, 2)).isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void ueberschneidungVonZeitraeumen() {
        assertThat(zeitraum(2, 5).ueberschneidetSich(zeitraum(4, 6))).isTrue();
        assertThat(zeitraum(2, 5).ueberschneidetSich(zeitraum(3, 4))).isTrue();
        assertThat(zeitraum(2, 5).ueberschneidetSich(zeitraum(1, 3))).isTrue();
        // Abreisetag = Anreisetag des naechsten: keine Ueberschneidung
        assertThat(zeitraum(2, 5).ueberschneidetSich(zeitraum(5, 7))).isFalse();
        assertThat(zeitraum(2, 5).ueberschneidetSich(zeitraum(1, 2))).isFalse();
    }

    // --- Belegung durch Gast

    @Test
    void freiesZimmerWirdFuerDenAufenthaltDurchGastBelegt() {
        var zimmer = neuesZimmer();
        var buchungsId = neueBuchungsId();

        zimmer.belegeDurchGast(buchungsId, HOTEL, zeitraum(2, 5));

        assertThat(zimmer.getBelegungen()).singleElement().satisfies(b -> {
            assertThat(b.getArt()).isEqualTo(Belegungsart.GAST);
            assertThat(b.getBuchungsId()).contains(buchungsId);
            assertThat(b.getZeitraum()).isEqualTo(zeitraum(2, 5));
        });
        assertThat(zimmer.istFrei(zeitraum(3, 4))).isFalse();
        assertThat(zimmer.istFrei(zeitraum(5, 6))).isTrue();
    }

    @Test
    void ueberschneidendeGastBelegungWirdAbgelehnt() {
        var zimmer = neuesZimmer();
        zimmer.belegeDurchGast(neueBuchungsId(), HOTEL, zeitraum(2, 5));

        assertThatThrownBy(() -> zimmer.belegeDurchGast(neueBuchungsId(), HOTEL, zeitraum(4, 6)))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("bereits belegt (GAST)");
    }

    @Test
    void geplanteWartungImAufenthaltVerhindertGastBelegung() {
        var zimmer = neuesZimmer();
        zimmer.planeWartung(WASSERSCHADEN, zeitraum(4, 6));

        assertThatThrownBy(() -> zimmer.belegeDurchGast(neueBuchungsId(), HOTEL, zeitraum(2, 5)))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("bereits belegt (WARTUNG)");
    }

    @Test
    void wartungAbAbreisetagVerhindertGastBelegungNicht() {
        var zimmer = neuesZimmer();
        zimmer.planeWartung(WASSERSCHADEN, zeitraum(5, 7));

        zimmer.belegeDurchGast(neueBuchungsId(), HOTEL, zeitraum(2, 5));

        assertThat(zimmer.getBelegungen()).hasSize(2);
    }

    @Test
    void zimmerKannNurVonBuchungenDesEigenenHotelsBelegtWerden() {
        var zimmer = neuesZimmer();

        assertThatThrownBy(() -> zimmer.belegeDurchGast(neueBuchungsId(), ANDERES_HOTEL, zeitraum(2, 5)))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("gehoert nicht zum Hotel");
    }

    // --- Checkout

    @Test
    void vorzeitigeAbreiseBeendetGastBelegungHeute() {
        var zimmer = neuesZimmer();
        var buchungsId = neueBuchungsId();
        zimmer.belegeDurchGast(buchungsId, HOTEL, zeitraum(1, 5));

        zimmer.beendeGastBelegung(buchungsId, HEUTE);

        assertThat(zimmer.getBelegungen()).singleElement()
            .extracting(Belegung::getZeitraum).isEqualTo(zeitraum(1, 2));
        assertThat(zimmer.istFrei(zeitraum(2, 5))).isTrue();
    }

    @Test
    void abreiseAmAnreisetagEntferntGastBelegung() {
        var zimmer = neuesZimmer();
        var buchungsId = neueBuchungsId();
        zimmer.belegeDurchGast(buchungsId, HOTEL, zeitraum(2, 5));

        zimmer.beendeGastBelegung(buchungsId, HEUTE);

        assertThat(zimmer.getBelegungen()).isEmpty();
    }

    @Test
    void abreiseAmGeplantenTagOderSpaeterLaesstBelegungUnveraendert() {
        var zimmer = neuesZimmer();
        var buchungsId = neueBuchungsId();
        zimmer.belegeDurchGast(buchungsId, HOTEL, zeitraum(-1, 2));

        zimmer.beendeGastBelegung(buchungsId, HEUTE);
        zimmer.beendeGastBelegung(buchungsId, HEUTE.plusDays(1));

        assertThat(zimmer.getBelegungen()).singleElement()
            .extracting(Belegung::getZeitraum).isEqualTo(zeitraum(-1, 2));
    }

    @Test
    void nurDieEigeneGastBelegungKannBeendetWerden() {
        var zimmer = neuesZimmer();
        zimmer.belegeDurchGast(neueBuchungsId(), HOTEL, zeitraum(1, 5));

        assertThatThrownBy(() -> zimmer.beendeGastBelegung(neueBuchungsId(), HEUTE))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("nicht durch diese Buchung belegt");
    }

    // --- Wartung

    @Test
    void wartungWaehrendEinesAufenthaltsWirdAbgelehnt() {
        var zimmer = neuesZimmer();
        zimmer.belegeDurchGast(neueBuchungsId(), HOTEL, zeitraum(2, 5));

        assertThatThrownBy(() -> zimmer.planeWartung(WASSERSCHADEN, zeitraum(3, 4)))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("bereits belegt");
    }

    @Test
    void laufendeWartungEndetHeute() {
        var zimmer = neuesZimmer();
        var wartung = zimmer.planeWartung(WASSERSCHADEN, zeitraum(1, 6));

        zimmer.beendeWartung(wartung, HEUTE);

        assertThat(zimmer.getBelegungen()).singleElement()
            .extracting(Belegung::getZeitraum).isEqualTo(zeitraum(1, 2));
        assertThat(zimmer.istFrei(zeitraum(2, 5))).isTrue();
    }

    @Test
    void nochNichtBegonneneWartungEntfaellt() {
        var zimmer = neuesZimmer();
        var wartung = zimmer.planeWartung(WASSERSCHADEN, zeitraum(2, 6));

        zimmer.beendeWartung(wartung, HEUTE);

        assertThat(zimmer.getBelegungen()).isEmpty();
    }

    @Test
    void nurGeplanteUndNichtAbgelaufeneWartungenKoennenBeendetWerden() {
        var zimmer = neuesZimmer();
        var gastBelegung = zimmer.belegeDurchGast(neueBuchungsId(), HOTEL, zeitraum(2, 5));
        var abgelaufen = zimmer.planeWartung(WASSERSCHADEN, zeitraum(-3, 1));

        assertThatThrownBy(() -> zimmer.beendeWartung(gastBelegung, HEUTE))
            .isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> zimmer.beendeWartung(new Belegung.BelegungId(UUID.randomUUID()), HEUTE))
            .isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> zimmer.beendeWartung(abgelaufen, HEUTE))
            .isInstanceOf(DomainAssertionException.class);
    }

    // --- Invarianten

    @Test
    void zimmerMitUeberschneidendenBelegungenIstUngueltig() {
        var belegungen = List.of(Belegung.durchWartung(WASSERSCHADEN, zeitraum(2, 5)),
            Belegung.durchGast(neueBuchungsId(), zeitraum(4, 6)));

        assertThatThrownBy(() -> new Zimmer(0, new Zimmer.ZimmerId(UUID.randomUUID()), HOTEL,
            new Zimmernummer("101"), Zimmerkategorie.DOPPELZIMMER, belegungen))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void gastBelegungBrauchtBuchungUndKeinenWartungsgrund() {
        assertThatThrownBy(() -> belegung(Belegungsart.GAST, null, null))
            .isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> belegung(Belegungsart.GAST, neueBuchungsId(), WASSERSCHADEN))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void wartungsBelegungBrauchtWartungsgrundUndKeineBuchung() {
        assertThatThrownBy(() -> belegung(Belegungsart.WARTUNG, null, null))
            .isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> belegung(Belegungsart.WARTUNG, neueBuchungsId(), WASSERSCHADEN))
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void zimmernummerUndWartungsgrundDuerfenNichtLeerSein() {
        assertThatThrownBy(() -> new Zimmernummer(" ")).isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> new Wartungsgrund("")).isInstanceOf(DomainAssertionException.class);
    }

    /** Zeitraum relativ zum November 2026: Tag 2 = heute. */
    private static Belegungszeitraum zeitraum(int vonTag, int bisTag) {
        var basis = LocalDate.of(2026, 11, 1);
        return new Belegungszeitraum(basis.plusDays(vonTag - 1), basis.plusDays(bisTag - 1));
    }

    private static Belegung belegung(Belegungsart art, Buchung.BuchungsId buchungsId, Wartungsgrund grund) {
        return new Belegung(0, new Belegung.BelegungId(UUID.randomUUID()), art, zeitraum(2, 5), buchungsId, grund);
    }

    private static Zimmer neuesZimmer() {
        return Zimmer.legeAn(HOTEL, new Zimmernummer("101"), Zimmerkategorie.DOPPELZIMMER);
    }

    private static Buchung.BuchungsId neueBuchungsId() {
        return new Buchung.BuchungsId(UUID.randomUUID());
    }
}
