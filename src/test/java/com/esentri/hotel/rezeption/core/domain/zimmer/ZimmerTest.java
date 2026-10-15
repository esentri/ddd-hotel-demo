package com.esentri.hotel.rezeption.core.domain.zimmer;

import com.esentri.hotel.rezeption.core.domain.buchung.Buchung;
import io.domainlifecycles.assertion.DomainAssertionException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ZimmerTest {

    private static final Buchung.BuchungId BUCHUNG = new Buchung.BuchungId(UUID.randomUUID());

    @Test
    void bezugsfertigesZimmerDerGewuenschtenKategorieWirdBelegt() {
        var zimmer = zimmer(Zimmerkategorie.DOPPELZIMMER, Zimmerstatus.BEZUGSFERTIG);

        zimmer.belege(BUCHUNG, Zimmerkategorie.DOPPELZIMMER);

        assertThat(zimmer.getStatus()).isEqualTo(Zimmerstatus.BELEGT);
        assertThat(zimmer.getBelegtDurch()).isEqualTo(BUCHUNG);
    }

    @Test
    void zimmerHoehererKategorieKannAlsUpgradeBelegtWerden() {
        var zimmer = zimmer(Zimmerkategorie.SUITE, Zimmerstatus.BEZUGSFERTIG);

        zimmer.belege(BUCHUNG, Zimmerkategorie.EINZELZIMMER);

        assertThat(zimmer.getStatus()).isEqualTo(Zimmerstatus.BELEGT);
    }

    @Test
    void zimmerNiedrigererKategorieKannNichtBelegtWerden() {
        var zimmer = zimmer(Zimmerkategorie.EINZELZIMMER, Zimmerstatus.BEZUGSFERTIG);

        assertThatThrownBy(() -> zimmer.belege(BUCHUNG, Zimmerkategorie.DOPPELZIMMER))
            .isInstanceOf(DomainAssertionException.class)
            .hasMessageContaining("Zimmerkategorie");
        assertThat(zimmer.getStatus()).isEqualTo(Zimmerstatus.BEZUGSFERTIG);
    }

    @Test
    void nurBezugsfertigeZimmerKoennenBelegtWerden() {
        for (var status : new Zimmerstatus[]{Zimmerstatus.ZU_REINIGEN, Zimmerstatus.AUSSER_BETRIEB}) {
            var zimmer = zimmer(Zimmerkategorie.SUITE, status);

            assertThatThrownBy(() -> zimmer.belege(BUCHUNG, Zimmerkategorie.SUITE))
                .isInstanceOf(DomainAssertionException.class)
                .hasMessageContaining("nicht bezugsfertig");
        }
    }

    @Test
    void belegtesZimmerKannNichtErneutBelegtWerden() {
        var zimmer = zimmer(Zimmerkategorie.SUITE, Zimmerstatus.BEZUGSFERTIG);
        zimmer.belege(BUCHUNG, Zimmerkategorie.SUITE);

        assertThatThrownBy(() -> zimmer.belege(new Buchung.BuchungId(UUID.randomUUID()), Zimmerkategorie.SUITE))
            .isInstanceOf(DomainAssertionException.class);
        assertThat(zimmer.getBelegtDurch()).isEqualTo(BUCHUNG);
    }

    @Test
    void belegtesZimmerGehoertImmerZuEinerBuchung() {
        assertThatThrownBy(() -> zimmerBuilder(Zimmerkategorie.SUITE, Zimmerstatus.BELEGT).build())
            .isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> zimmerBuilder(Zimmerkategorie.SUITE, Zimmerstatus.BEZUGSFERTIG).belegtDurch(BUCHUNG).build())
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void pflichtmerkmale() {
        assertThatThrownBy(() -> zimmerBuilder(null, Zimmerstatus.BEZUGSFERTIG).build()).isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> zimmerBuilder(Zimmerkategorie.SUITE, Zimmerstatus.BEZUGSFERTIG).zimmernummer(null).build())
            .isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> zimmerBuilder(Zimmerkategorie.SUITE, Zimmerstatus.BEZUGSFERTIG).blickrichtung(null).build())
            .isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> zimmerBuilder(Zimmerkategorie.SUITE, Zimmerstatus.BEZUGSFERTIG).bettart(null).build())
            .isInstanceOf(DomainAssertionException.class);
    }

    @Test
    void zimmernummerIstPflichtUndHoechstens10Zeichen() {
        assertThatThrownBy(() -> new Zimmernummer(" ")).isInstanceOf(DomainAssertionException.class);
        assertThatThrownBy(() -> new Zimmernummer("12345678901")).isInstanceOf(DomainAssertionException.class);
    }

    private static Zimmer zimmer(Zimmerkategorie kategorie, Zimmerstatus status) {
        return zimmerBuilder(kategorie, status).build();
    }

    private static Zimmer.ZimmerBuilder zimmerBuilder(Zimmerkategorie kategorie, Zimmerstatus status) {
        return Zimmer.builder()
            .id(new Zimmer.ZimmerId(UUID.randomUUID()))
            .zimmernummer(new Zimmernummer("101"))
            .zimmerkategorie(kategorie)
            .etage(1)
            .blickrichtung(Blickrichtung.PARK)
            .balkon(true)
            .barrierefrei(false)
            .bettart(Bettart.DOPPELBETT)
            .raucherzimmer(false)
            .status(status);
    }
}
