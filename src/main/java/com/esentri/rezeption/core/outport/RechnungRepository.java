package com.esentri.rezeption.core.outport;

import com.esentri.rezeption.core.domain.rechnung.Rechnung;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import nitrox.dlc.domain.types.Repository;

import java.util.List;

/**
 * Das RechnungRepository beschreibt Methoden für den Zugriff auf Rechnungen in der zugrundeliegenden Datenquelle.
 *
 * Es definiert die Möglichkeit, Rechnungen basierend auf der Reservierungsnummer zu finden.
 *
 * @author Mario Herb
 */
public interface RechnungRepository extends Repository<Rechnung.RechnungId, Rechnung> {

    /**
     * Sucht alle Rechnungen, die der spezifizierten Reservierungsnummer zugeordnet sind.
     *
     * @param reservierungsNummer Die Reservierungsnummer, nach der gesucht werden soll.
     * @return Eine Liste von Rechnungen, die der bereitgestellten Reservierungsnummer entsprechen.
     */
    List<Rechnung> findByReservierungsNummer(Reservierung.ReservierungsNummer reservierungsNummer);

}
