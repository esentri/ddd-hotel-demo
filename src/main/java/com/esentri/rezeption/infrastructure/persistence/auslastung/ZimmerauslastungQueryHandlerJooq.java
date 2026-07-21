package com.esentri.rezeption.infrastructure.persistence.auslastung;

import com.esentri.rezeption.domain.auslastung.Zimmerauslastung;
import com.esentri.rezeption.domain.auslastung.ZimmerauslastungQueryHandler;
import com.esentri.rezeption.domain.zimmer.Zimmerkategorie;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.table;

/**
 * jOOQ-basierte Implementierung des ZimmerauslastungQueryHandlers.
 * Nutzt die Datenbank-View rezeption.v_zimmerauslastung.
 */
@Repository
public class ZimmerauslastungQueryHandlerJooq implements ZimmerauslastungQueryHandler {

    private final DSLContext dslContext;

    public ZimmerauslastungQueryHandlerJooq(DSLContext dslContext) {
        this.dslContext = Objects.requireNonNull(dslContext, "DSLContext darf nicht null sein");
    }

    @Override
    public List<Zimmerauslastung> findeZimmerauslastung(LocalDate von, LocalDate bis, Zimmerkategorie zimmerkategorie) {
        Condition condition = field(name("DATUM")).greaterOrEqual(von)
            .and(field(name("DATUM")).lessOrEqual(bis));

        if (zimmerkategorie != null) {
            condition = condition.and(field(name("KATEGORIE")).eq(zimmerkategorie.name()));
        }

        return dslContext.select(
                field(name("DATUM"), LocalDate.class),
                field(name("KATEGORIE"), String.class),
                field(name("ANZAHL_BELEGT"), Integer.class),
                field(name("ANZAHL_GESAMT"), Integer.class)
            )
            .from(table(name("REZEPTION", "V_ZIMMERAUSLASTUNG")))
            .where(condition)
            .orderBy(field(name("DATUM")), field(name("KATEGORIE")))
            .fetch(record -> new Zimmerauslastung(
                record.get(field(name("DATUM"), LocalDate.class)),
                Zimmerkategorie.valueOf(record.get(field(name("KATEGORIE"), String.class))),
                record.get(field(name("ANZAHL_BELEGT"), Integer.class)),
                record.get(field(name("ANZAHL_GESAMT"), Integer.class))
            ));
    }
}
