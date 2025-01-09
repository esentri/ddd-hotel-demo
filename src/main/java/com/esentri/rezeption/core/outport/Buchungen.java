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
import com.esentri.rezeption.core.domain.buchung.Buchung;
import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import io.domainlifecycles.domain.types.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository für Buchungen.
 *
 * @author Mario Herb
 */
public interface Buchungen extends Repository<Buchung.BuchungsNummer, Buchung> {

    /**
     * Findet und gibt eine Liste von aktiven Buchungen (nicht ausgecheckt und nicht storniert) für ein bestimmtes Hotel zurück, die bestimmte Kriterien erfüllen.
     *
     * @param hotelId das Id des Hotels
     * @param von  Datum an dem die Buchungen anfangen
     * @param bis  Datum an dem die Buchungen enden
     * @param gewuenschteKategorie die gewünschte Zimmerkategorie (optional)
     * @param gewuenschteKapazitaet die gewünschte Zimmerkapazität (optional)
     * @return eine Liste von Buchungen
     */
    List<Buchung> listAktiveBuchungenInZeitraum(Hotel.Id hotelId, LocalDate von, LocalDate bis, ZimmerKategorie gewuenschteKategorie, Integer gewuenschteKapazitaet);
}