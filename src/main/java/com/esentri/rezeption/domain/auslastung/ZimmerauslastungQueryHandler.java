package com.esentri.rezeption.domain.auslastung;

import com.esentri.rezeption.domain.zimmer.Zimmerkategorie;
import io.domainlifecycles.domain.types.QueryHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * QueryHandler zur Abfrage der Zimmerauslastung.
 */
public interface ZimmerauslastungQueryHandler extends QueryHandler<Zimmerauslastung> {

    /**
     * Ermittelt die Zimmerauslastung fuer einen bestimmten Zeitraum und optional eine Kategorie.
     *
     * @param von             Beginn des Zeitraums (inklusiv)
     * @param bis             Ende des Zeitraums (inklusiv)
     * @param zimmerkategorie zimmerkategorie als Filter
     * @return Liste der Zimmerauslastungen pro Tag und Kategorie
     */
    List<Zimmerauslastung> findeZimmerauslastung(LocalDate von, LocalDate bis, Zimmerkategorie zimmerkategorie);
}
