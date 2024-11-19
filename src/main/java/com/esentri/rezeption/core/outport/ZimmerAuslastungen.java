package com.esentri.rezeption.core.outport;

import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.zimmer.ZimmerAuslastung;
import io.domainlifecycles.domain.types.QueryHandler;

import java.time.LocalDate;
import java.util.List;

/**
 * QueryHandler, der das ZimmerAuslastung ReadModel für die Auslastung eines Hotels in einem bestimmten Zeitraum
 * zurückliefert.
 *
 * @author Mario Herb
 */
public interface ZimmerAuslastungen extends QueryHandler<ZimmerAuslastung> {

    /**
     * Berechnet und liefert eine Liste von ZimmerAuslastungen für ein bestimmtes Hotel und einen bestimmten Zeitraum.
     *
     * @param hotelId Identität des Hotels
     * @param von Beginndatum des Zeitraums
     * @param bis Enddatum des Zeitraums
     * @return Eine Liste von ZimmerAuslastung Objekten mit der Anzahl der belegten und freien Zimmer für jeden Tag des gewählten Zeitraums.
     * @throws IllegalStateException wenn hotelId, von, bis nicht korrekt definiert sind
     */
    List<ZimmerAuslastung> auslastung(Hotel.Id hotelId, LocalDate von, LocalDate bis);
}
