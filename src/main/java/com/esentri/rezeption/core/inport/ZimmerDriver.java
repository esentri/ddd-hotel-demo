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

package com.esentri.rezeption.core.inport;

import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.reservierung.ReservierungAusgecheckt;
import com.esentri.rezeption.core.domain.zimmer.BeantrageZimmerWartung;
import com.esentri.rezeption.core.domain.zimmer.Zimmer;
import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import com.esentri.rezeption.core.domain.zimmer.ZimmerWartungEingeplant;
import com.esentri.rezeption.outbound.ZimmerAuslastung;
import nitrox.dlc.domain.types.Driver;

import java.time.LocalDate;
import java.util.List;

/**
 * Die Schnittstelle ZimmerDriver ermöglicht den Zugriff und die Manipulation von Zimmerdaten.
 * Sie bietet Methoden zum Abfragen verfügbarer Zimmer und zum Planen der Zimmerwartung.
 *
 * @author Mario Herb
 */
public interface ZimmerDriver extends Driver {

    /**
     * Behandelt das Ereignis, dass eine Zimmerwartung geplant wurde.
     * Diese Methode wird aufgerufen, wenn eine Wartung für ein Zimmer eingeplant wird.
     *
     * @param zimmerWartungEingeplant das Ereignis, das eine geplante Zimmerwartung repräsentiert
     */
    void onEvent(ZimmerWartungEingeplant zimmerWartungEingeplant);

    /**
     * Behandelt das Ereignis, dass eine Reservierung ausgecheckt wurde.
     * Diese Methode wird aufgerufen, wenn ein Gast aus seinem reservierten Zimmer ausgecheckt hat.
     *
     * Dabei soll die Belegung des Zimmers beendet werden.
     *
     * @param reservierungAusgecheckt das Ereignis, das ein ausgechecktes Reservierung repräsentiert
     */
    void onEvent(ReservierungAusgecheckt reservierungAusgecheckt);

    /**
     * Gibt eine Liste von verfügbaren Zimmernummer für ein spezifisches Hotel und einen bestimmten Zeitraum zurück.
     * Diese Methode wird aufgerufen, um zu überprüfen, welche Zimmer zu gegebenen Kriterien verfügbar sind.
     *
     * @param hotelId die ID des Hotels, für das die verfügbaren Zimmer ermittelt werden sollen
     * @param von das Anfangsdatum des Zeitraums
     * @param bis das Enddatum des Zeitraums
     * @param zimmerKategorie die gewünschte Zimmerkategorie
     * @param kapazitaet die benötige Kapazität
     * @return eine Liste von Zimmernummern der verfügbaren Zimmer
     */
    List<Zimmer.ZimmerNummer> verfuegbareZimmer(Hotel.HotelId hotelId, LocalDate von, LocalDate bis, ZimmerKategorie zimmerKategorie, int kapazitaet);

    /**
     * Behandelt die Anforderung einer Zimmerwartung.
     * Diese Methode wird aufgerufen, wenn eine Wartung für ein Zimmer beantragt wird.
     *
     * @param beantrageZimmerWartung das Command, das eine beantragte Zimmerwartung repräsentiert
     */
    void handle(BeantrageZimmerWartung beantrageZimmerWartung);


    /**
     * Berechnet und liefert eine Liste von ZimmerAuslastungen für ein bestimmtes Hotel und einen bestimmten Zeitraum.
     *
     * @param hotelId Identität des Hotels
     * @param von Beginndatum des Zeitraums
     * @param bis Enddatum des Zeitraums
     * @return Eine Liste von ZimmerAuslastung Objekten mit der Anzahl der belegten und freien Zimmer für jeden Tag des gewählten Zeitraums.
     * @throws IllegalStateException wenn hotelId, von, bis nicht korrekt definiert sind
     */
    List<ZimmerAuslastung> auslastung(Hotel.HotelId hotelId, LocalDate von, LocalDate bis);

}