package com.esentri.rezeption.outbound;

import com.esentri.rezeption.core.base.BaseInMemoryRepository;
import com.esentri.rezeption.core.domain.rechnung.Rechnung;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.outport.RechnungRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Eine konkrete Implementierung des RechnungRepository.
 * Diese Klasse erbt die Funktionalität von BaseInMemoryRepository und implementiert die spezifische Logik
 * zum Abrufen von Rechnungen nach Reservierungsnummern.
 * Es wird mit der @Repository Annotation gekennzeichnet, um die automatische Erkennung
 * durch den Spring Framework Component Scan zu ermöglichen.
 *
 * @author Mario Herb
 * @see BaseInMemoryRepository
 * @see RechnungRepository
 * @see Rechnung
 * @see Reservierung
 */

@Repository
public class RechnungRepositoryImpl extends BaseInMemoryRepository<Rechnung.RechnungId, Rechnung> implements RechnungRepository {

    public RechnungRepositoryImpl() {
        super(new ArrayList<>());
    }

    /**
     * Findet und gibt Rechnungsobjekte zurück, die mit der gegebenen Reservierungsnummer verknüpft sind.
     * Es verwendet die Java-Stream-API, um den Rechnungen-Stream zu filtern und
     * alle Rechnungen zu sammeln, die die Bedingung erfüllen.
     *
     * @param reservierungsNummer die Reservierungsnummer, nach der gesucht wird.
     * @return Eine Liste von Rechnungsobjekten, die mit der angegebenen Reservierungsnummer verknüpft sind.
     */
    @Override
    public List<Rechnung> findByReservierungsNummer(Reservierung.ReservierungsNummer reservierungsNummer) {
        return allAggregates.stream().filter(r -> reservierungsNummer.equals(r.getReservierungsNummer())).toList();
    }
}