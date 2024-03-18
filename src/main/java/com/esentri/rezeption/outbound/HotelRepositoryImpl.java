package com.esentri.rezeption.outbound;

import com.esentri.rezeption.core.base.BaseInMemoryRepository;
import com.esentri.rezeption.core.domain.Adresse;
import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.outport.HotelRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

/**
 * Diese Klasse ist eine Implementierung des HotelRepository-Interfaces und erweitert das BaseInMemoryRepository mit Hotel als Entitätstyp und HotelId als Identitätstyp.
 * Sie nutzt die In-Memory-Speicherstrategie zur Verwaltung von Hotel-Objekten.
 * Beim Erzeugen der Instanz wird bereits ein Hotel-Objekt zur Liste aller Aggregate hinzugefügt.
 *
 *
 *
 * @author Mario Herb
 * @see BaseInMemoryRepository
 * @see HotelRepository
 * @see Repository
 */
@Repository
public class HotelRepositoryImpl extends BaseInMemoryRepository<Hotel.HotelId, Hotel> implements HotelRepository {

    /**
     * Der Konstruktor erstellt eine Instanz von HotelRepositoryImpl und initialisiert das zugehörige In-Memory-Repository mit einer leeren Liste.
     * Anschließend fügt er ein Hotel-Objekt mit Testdaten zur Liste der Aggregate hinzu.
     * Solche Initialisierung wird in der Regel für Testzwecke verwendet.
     */
    public HotelRepositoryImpl() {
        super(new ArrayList<>());
        allAggregates.add(
                new Hotel(
                        TestDataIds.HOTEL_ID.id(),
                        "MeinSuperHotel",
                        Adresse.builder()
                                .ort("Karlsruhe")
                                .strasse("Prachtstrasse")
                                .hausnummer("1a")
                                .postleitzahl("76133")
                                .build()
                )
        );
    }
}
