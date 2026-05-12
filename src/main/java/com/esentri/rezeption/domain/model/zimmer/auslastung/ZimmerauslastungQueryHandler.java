package com.esentri.rezeption.domain.model.zimmer.auslastung;

import com.esentri.rezeption.domain.model.Zeitraum;
import io.domainlifecycles.domain.types.QueryHandler;

import java.util.List;

/**
 * QueryHandler für die Abfrage der Zimmerauslastung.
 */
public interface ZimmerauslastungQueryHandler extends QueryHandler<Zimmerauslastung> {

    /**
     * Ermittelt die Zimmerauslastung für einen Zeitraum.
     * @param zeitraum Der betrachtete Zeitraum.
     * @return Liste der Zimmerauslastungen pro Tag und Kategorie.
     */
    List<Zimmerauslastung> ermittleAuslastung(Zeitraum zeitraum);
}
