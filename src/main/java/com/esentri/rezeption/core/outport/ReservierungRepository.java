package com.esentri.rezeption.core.outport;

import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.reservierung.Reservierung;
import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import nitrox.dlc.domain.types.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository für Buchungen.
 *
 * @author Mario Herb
 */
public interface ReservierungRepository extends Repository<Reservierung.ReservierungsNummer, Reservierung> {

    /**
     * Diese Methode liefert eine Liste von aktiven Buchungen in einem bestimmten Zeitraum.
     * Sie nimmt als Parameter die Hotel-ID für welches die Buchungen gelistet werden sollen,
     * den Start- und Endzeitpunkt des Zeitraums, die gewünschte Zimmerkategorie
     * und die gewünschte Kapazität der Zimmer.
     *
     * @param hotelId Die ID des Hotels, für das die Buchungen gelistet werden sollen.
     * @param von Das Startdatum des Zeitraums, in dem die Buchungen gelistet werden sollen.
     * @param bis Das Enddatum des Zeitraums, in dem die Buchungen gelistet werden sollen.
     * @param gewuenschteKategorie Die gewünschte Zimmerkategorie.
     * @param gewuenschteKapazitaet Die gewünschte Kapazität der Zimmer.
     * @return Eine Liste von aktiven Buchungen, die den übergebenen Kriterien entsprechen.
     */
    List<Reservierung> listAktiveBuchungenInZeitraum(
            Hotel.HotelId hotelId,
            LocalDate von,
            LocalDate bis,
            ZimmerKategorie gewuenschteKategorie,
            int gewuenschteKapazitaet
    );
}