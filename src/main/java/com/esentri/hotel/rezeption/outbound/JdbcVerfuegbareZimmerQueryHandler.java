package com.esentri.hotel.rezeption.outbound;

import com.esentri.hotel.rezeption.core.domain.zimmer.Bettart;
import com.esentri.hotel.rezeption.core.domain.zimmer.Blickrichtung;
import com.esentri.hotel.rezeption.core.domain.zimmer.VerfuegbareZimmerQueryHandler;
import com.esentri.hotel.rezeption.core.domain.zimmer.VerfuegbaresZimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerkategorie;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmernummer;
import com.esentri.hotel.rezeption.core.domain.zimmer.Zimmerstatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

class JdbcVerfuegbareZimmerQueryHandler implements VerfuegbareZimmerQueryHandler {

    private static final String SQL = """
        SELECT id, zimmernummer_value, zimmerkategorie, etage, blickrichtung, balkon, barrierefrei, bettart, raucherzimmer
          FROM zimmer
         WHERE status = :status
           AND zimmerkategorie IN (:zimmerkategorien)
        """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    JdbcVerfuegbareZimmerQueryHandler(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<VerfuegbaresZimmer> findeVerfuegbareZimmer(Zimmerkategorie gewuenschteZimmerkategorie) {
        var parameter = new MapSqlParameterSource()
            .addValue("status", Zimmerstatus.BEZUGSFERTIG.name())
            .addValue("zimmerkategorien", gewuenschteZimmerkategorie.gleichOderHoeher().stream().map(Enum::name).toList());
        return jdbcTemplate.query(SQL, parameter, (rs, rowNum) -> {
                var zimmerkategorie = Zimmerkategorie.valueOf(rs.getString("zimmerkategorie"));
                return new VerfuegbaresZimmer(
                    new Zimmer.ZimmerId(UUID.fromString(rs.getString("id"))),
                    new Zimmernummer(rs.getString("zimmernummer_value")),
                    zimmerkategorie,
                    zimmerkategorie != gewuenschteZimmerkategorie,
                    rs.getInt("etage"),
                    Blickrichtung.valueOf(rs.getString("blickrichtung")),
                    rs.getBoolean("balkon"),
                    rs.getBoolean("barrierefrei"),
                    Bettart.valueOf(rs.getString("bettart")),
                    rs.getBoolean("raucherzimmer"));
            }).stream()
            .sorted(Comparator.comparing(VerfuegbaresZimmer::zimmerkategorie)
                .thenComparing(z -> z.zimmernummer().value()))
            .toList();
    }
}
