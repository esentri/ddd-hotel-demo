/*
 *  Copyright 2024 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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