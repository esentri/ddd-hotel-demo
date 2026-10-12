package com.esentri.hotel.rezeption.core.domain.verfuegbarkeit;

import com.esentri.hotel.rezeption.core.domain.hotel.HotelId;
import io.domainlifecycles.domain.types.QueryHandler;

import java.time.LocalDate;

public interface FreieZimmerQueryHandler extends QueryHandler<FreieZimmerUebersicht> {

    /**
     * @param von erster Tag der Uebersicht
     * @param bis erster Tag nach der Uebersicht (ausschliesslich)
     */
    FreieZimmerUebersicht freieZimmer(HotelId hotelId, LocalDate von, LocalDate bis);
}
