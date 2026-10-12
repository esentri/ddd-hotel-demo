package com.esentri.rezeption.infrastructure.persistence.auslastung;

import com.esentri.rezeption.domain.auslastung.Zimmerauslastung;
import com.esentri.rezeption.domain.auslastung.ZimmerauslastungQueryHandler;
import com.esentri.rezeption.domain.zimmer.Zimmerkategorie;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * JDBC-basierte Implementierung des ZimmerauslastungQueryHandlers.
 * Nutzt die Datenbank-View rezeption.v_zimmerauslastung.
 */
@Repository
public class ZimmerauslastungQueryHandlerJdbc implements ZimmerauslastungQueryHandler {

    private final JdbcTemplate jdbcTemplate;

    public ZimmerauslastungQueryHandlerJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "JdbcTemplate darf nicht null sein");
    }

    @Override
    public List<Zimmerauslastung> findeZimmerauslastung(LocalDate von, LocalDate bis, Zimmerkategorie zimmerkategorie) {
        StringBuilder sql = new StringBuilder(
            "SELECT DATUM, KATEGORIE, ANZAHL_BELEGT, ANZAHL_GESAMT FROM REZEPTION.V_ZIMMERAUSLASTUNG"
                + " WHERE DATUM >= ? AND DATUM <= ?");
        List<Object> parameter = new ArrayList<>(List.of(von, bis));
        if (zimmerkategorie != null) {
            sql.append(" AND KATEGORIE = ?");
            parameter.add(zimmerkategorie.name());
        }
        sql.append(" ORDER BY DATUM, KATEGORIE");

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> new Zimmerauslastung(
                rs.getObject("DATUM", LocalDate.class),
                Zimmerkategorie.valueOf(rs.getString("KATEGORIE")),
                rs.getInt("ANZAHL_BELEGT"),
                rs.getInt("ANZAHL_GESAMT")),
            parameter.toArray());
    }
}
