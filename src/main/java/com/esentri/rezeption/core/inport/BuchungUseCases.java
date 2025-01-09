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
import com.esentri.rezeption.core.domain.buchung.Buchung;
import com.esentri.rezeption.core.domain.buchung.ErstelleNeueBuchung;
import com.esentri.rezeption.core.domain.buchung.StorniereBuchung;
import com.esentri.rezeption.core.domain.hotel.Hotel;
import com.esentri.rezeption.core.domain.buchung.CheckeAus;
import com.esentri.rezeption.core.domain.buchung.CheckeEin;
import com.esentri.rezeption.core.domain.buchung.VervollstaendigeGastDaten;
import com.esentri.rezeption.core.domain.zimmer.ZimmerKategorie;
import io.domainlifecycles.domain.types.ApplicationService;

import java.time.LocalDate;
import java.util.List;

/**
 * Dieses Interface bietet eine Methode an, um verschiedene Commands für Buchungen zu handhaben. Diese Commands umfassen die Erstellung
 * einer neuen Buchung, das Aktualisieren von Gastdaten, das Ein- und Auschecken und das Stornieren einer Buchung.
 *
 * @author Mario Herb
 */
public interface BuchungUseCases extends ApplicationService {

    /**
     * Behandelt den Command zur Erstellung einer neuen Buchung.
     * @param erstelleNeueBuchung beinhaltet alle notwenigen Informationen zur Erstellung einer neuen Buchung.
     * @return Gibt die Nummer der erstellten Buchung zurück.
     */
    Buchung.BuchungsNummer handle(ErstelleNeueBuchung erstelleNeueBuchung);

    /**
     * Behandelt Command zur der Aktualisierung von Gastdaten.
     * @param vervollstaendigeGastDaten beinhaltet alle notwenigen Informationen zur Aktualisierung der Gastdaten.
     * @return Gibt die Nummer der Buchung zurück, für die die Gastdaten aktualisiert wurden.
     */
    Buchung.BuchungsNummer handle(VervollstaendigeGastDaten vervollstaendigeGastDaten);

    /**
     * Behandelt den Check-In Command.
     * @param checkeEin beinhaltet alle notwenigen Informationen für den Check-In eines Gastes.
     * @return Gibt die Nummer der Buchung zurück, für die der Check-In durchgeführt wurde.
     */
    Buchung.BuchungsNummer handle(CheckeEin checkeEin);

    /**
     * Behandelt den Check-Out Command.
     * @param checkeAus beinhaltet alle notwenigen Informationen für den Check-Out eines Gastes.
     * @return Gibt die Nummer der Buchung zurück, für die der Check-Out durchgeführt wurde.
     */
    Buchung.BuchungsNummer handle(CheckeAus checkeAus);

    /**
     * Behandelt das Stornieren einer Buchung.
     * @param storniereBuchung beinhaltet alle notwenigen Informationen um eine Buchung zu stornieren.
     * @return Gibt die Nummer der Buchung zurück, die storniert wurde.
     */
    Buchung.BuchungsNummer handle(StorniereBuchung storniereBuchung);

    /**
     * Findet und gibt eine Liste von aktiven Buchungen (nicht storniert und nicht beendet) für ein bestimmtes Hotel zurück, die bestimmte Kriterien erfüllen.
     *
     * @param hotelId das Id des Hotels
     * @param von  Datum an dem die Buchungen anfangen
     * @param bis  Datum an dem die Buchungen enden
     * @param gewuenschteKategorie die gewünschte Zimmerkategorie (optional)
     * @param gewuenschteKapazitaet die gewünschte Zimmerkapazität (optional)
     * @return eine Liste von Buchungen
     */
    List<Buchung> listAktiveBuchungenInZeitraum(
            Hotel.Id hotelId,
            LocalDate von,
            LocalDate bis,
            ZimmerKategorie gewuenschteKategorie,
            Integer gewuenschteKapazitaet
    );

}
