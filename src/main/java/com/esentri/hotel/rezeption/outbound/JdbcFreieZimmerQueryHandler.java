package com.esentri.hotel.rezeption.outbound;

import com.esentri.hotel.rezeption.core.domain.buchung.BuchungsStatus;
import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import com.esentri.hotel.rezeption.core.domain.verfuegbarkeit.FreieZimmerAmTag;
import com.esentri.hotel.rezeption.core.domain.verfuegbarkeit.FreieZimmerQueryHandler;
import com.esentri.hotel.rezeption.core.domain.verfuegbarkeit.FreieZimmerUebersicht;
import com.esentri.hotel.rezeption.core.domain.verfuegbarkeit.VerfuegbarkeitJeKategorie;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Liest Zimmer, Belegungen und offene Reservierungen direkt per SQL, ohne die Aggregate zu laden.
 */
public class JdbcFreieZimmerQueryHandler implements FreieZimmerQueryHandler {

    private final JdbcClient jdbcClient;

    public JdbcFreieZimmerQueryHandler(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public FreieZimmerUebersicht freieZimmer(HotelId hotelId, LocalDate von, LocalDate bis) {
        var zimmer = jdbcClient.sql("SELECT id, zimmernummer_value, kategorie FROM zimmer WHERE hotel_id = :hotelId")
            .param("hotelId", hotelId.value())
            .query((rs, i) -> new Zimmer(rs.getObject("id", UUID.class),
                new Zimmernummer(rs.getString("zimmernummer_value")),
                Zimmerkategorie.valueOf(rs.getString("kategorie"))))
            .list()
            .stream()
            .sorted(Comparator.comparing(z -> z.nummer().value()))
            .toList();
        var belegungen = jdbcClient.sql("""
                SELECT b.zimmer_id, b.zeitraum_von, b.zeitraum_bis
                FROM belegung b JOIN zimmer z ON z.id = b.zimmer_id
                WHERE z.hotel_id = :hotelId AND b.zeitraum_von < :bis AND b.zeitraum_bis > :von
                """)
            .param("hotelId", hotelId.value())
            .param("von", von)
            .param("bis", bis)
            .query((rs, i) -> new Belegung(rs.getObject("zimmer_id", UUID.class),
                rs.getObject("zeitraum_von", LocalDate.class), rs.getObject("zeitraum_bis", LocalDate.class)))
            .list();
        var offeneReservierungen = jdbcClient.sql("""
                SELECT aufenthaltszeitraum_anreise, aufenthaltszeitraum_abreise, zimmerkategorie
                FROM buchung
                WHERE hotel_id = :hotelId AND status = :reserviert
                  AND aufenthaltszeitraum_anreise < :bis AND aufenthaltszeitraum_abreise > :von
                """)
            .param("hotelId", hotelId.value())
            .param("reserviert", BuchungsStatus.RESERVIERT.name())
            .param("von", von)
            .param("bis", bis)
            .query((rs, i) -> new Reservierung(rs.getObject("aufenthaltszeitraum_anreise", LocalDate.class),
                rs.getObject("aufenthaltszeitraum_abreise", LocalDate.class),
                Zimmerkategorie.valueOf(rs.getString("zimmerkategorie"))))
            .list();

        var tage = von.datesUntil(bis)
            .map(tag -> new FreieZimmerAmTag(tag, Arrays.stream(Zimmerkategorie.values())
                .map(kategorie -> verfuegbarkeit(tag, kategorie, zimmer, belegungen, offeneReservierungen))
                .toList()))
            .toList();
        return new FreieZimmerUebersicht(hotelId, von, bis, zimmer.size(), tage);
    }

    private static VerfuegbarkeitJeKategorie verfuegbarkeit(LocalDate tag, Zimmerkategorie kategorie,
                                                            List<Zimmer> zimmer, List<Belegung> belegungen,
                                                            List<Reservierung> offeneReservierungen) {
        var frei = zimmer.stream()
            .filter(z -> z.kategorie() == kategorie)
            .filter(z -> belegungen.stream().noneMatch(b -> b.zimmerId().equals(z.id()) && b.belegt(tag)))
            .map(Zimmer::nummer)
            .toList();
        var reserviert = (int) offeneReservierungen.stream()
            .filter(r -> r.kategorie() == kategorie && r.umfasst(tag))
            .count();
        return new VerfuegbarkeitJeKategorie(kategorie, frei.size(), frei, reserviert, frei.size() - reserviert);
    }

    private record Zimmer(UUID id, Zimmernummer nummer, Zimmerkategorie kategorie) {
    }

    private record Belegung(UUID zimmerId, LocalDate von, LocalDate bis) {

        boolean belegt(LocalDate tag) {
            return !tag.isBefore(von) && tag.isBefore(bis);
        }
    }

    private record Reservierung(LocalDate anreise, LocalDate abreise, Zimmerkategorie kategorie) {

        boolean umfasst(LocalDate tag) {
            return !tag.isBefore(anreise) && tag.isBefore(abreise);
        }
    }
}
