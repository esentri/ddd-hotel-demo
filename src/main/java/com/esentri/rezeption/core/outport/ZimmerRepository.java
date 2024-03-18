package com.esentri.rezeption.core.outport;

import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import nitrox.dlc.domain.types.Repository;

import java.util.List;

/**
 * Das ZimmerRepository Interface bietet Methoden zum Abrufen von Informationen über Zimmer in einem spezifizierten Hotel.
 * Die Methoden erlauben das Auflisten von Zimmern basierend auf dem Status, der Kategorie und der Kapazität.
 *
 * @author Mario Herb
 */
public interface ZimmerRepository extends Repository<Zimmer.ZimmerNummer, Zimmer> {

    /**
     * Gibt eine Liste der Zimmer einer bestimmten Kategorie und Kapazität für ein spezifiziertes Hotel zurück.
     *
     * @param hotelId Die ID des Hotels, für das die Zimmer aufgelistet werden sollen
     * @param zimmerKategorie Die Kategorie der Zimmer, die aufgelistet werden sollen
     * @param kapazitaet Die Kapazität der Zimmer, die aufgelistet werden sollen
     * @return Eine Liste der Zimmer der bestimmten Kategorie und Kapazität
     */
    List<Zimmer> listZimmerByKategorieAndKapazitaet(Hotel.HotelId hotelId, ZimmerKategorie zimmerKategorie, int kapazitaet);

    /**
     * Gibt eine Liste der Zimmer für ein spezifiziertes Hotel zurück.
     *
     * @param hotelId Die ID des Hotels, für das die Zimmer aufgelistet werden sollen
     * @return Eine Liste der Zimmer für das angegebene Hotel
     */
    List<Zimmer> listByHotel(Hotel.HotelId hotelId);
}