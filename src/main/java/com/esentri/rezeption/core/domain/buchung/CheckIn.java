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

package com.esentri.rezeption.core.domain.buchung;

import com.esentri.rezeption.core.domain.zimmer.BelegungTyp;
import com.esentri.rezeption.core.outport.Buchungen;
import com.esentri.rezeption.core.outport.DomainEventPublisher;
import com.esentri.rezeption.core.outport.ZimmerVerwaltung;
import io.domainlifecycles.domain.types.DomainService;
import io.domainlifecycles.domain.types.Publishes;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

/**
 * Dieser Service übernimmt die Verarbeitung der CheckIn-Operationen.
 * Er integriert mehrere Repositories, um relevante Daten abzurufen und zu aktualisieren.
 * Er verwendet auch einen DomainEventPublisher, um relevante Ereignisse zu veröffentlichen.
 *
 * @author Mario Herb
 */
@RequiredArgsConstructor
public class CheckIn implements DomainService {

    private final Buchungen buchungen;

    private final ZimmerVerwaltung zimmerVerwaltung;

    private final DomainEventPublisher domainEventPublisher;

    /**
     * Verarbeitet den CheckeEin Command für eine Buchung.
     * Dabei wird zuerst die Buchung anhand der Buchungsnummer abgerufen und aktualisiert. Anschließend wird das Zimmer anhand der Zimmernummer ermittelt.
     * Schließlich wird das Check-In-Datum und das Check-Out-Datum berechnet und festgelegt.
     * Wenn das Zimmer für den gewünschten Zeitraum zur Verfügung steht, wird ein Check-In durchgeführt und das entsprechende Ereignis veröffentlicht.
     * Ansonsten wird eine IllegalStateException geworfen.
     *
     * @param checkeEin Informationen zum Check-In-Vorgang.
     * @return Buchungsnummer der erfolgreich eingecheckten Buchung.
     * @throws IllegalStateException Wenn das Zimmer zum gewünschten Zeitpunkt bereits belegt ist.
     */
    @Publishes(domainEventTypes = BuchungEingecheckt.class)
    public Buchung.BuchungsNummer handle(CheckeEin checkeEin){
        var buchung = buchungen.findById(checkeEin.buchungsNummer()).orElseThrow();
        buchungen.update(buchung.handle(checkeEin));

        var zimmer = zimmerVerwaltung.findById(checkeEin.zimmerNummer()).orElseThrow();

        var checkInAm = LocalDate.now();
        var checkOutAm = LocalDate.now().plusDays(checkeEin.geplanteAnzahlNaechte());

        if(zimmer.neueBelegung(LocalDate.now(), checkOutAm, BelegungTyp.GAST)){
            zimmerVerwaltung.update(zimmer);
            domainEventPublisher.publish(
                    new BuchungEingecheckt(
                            buchung.id(),
                            checkeEin.zimmerNummer(),
                            checkInAm,
                            checkOutAm
                    )
            );
            return  buchung.id();
        }else{
            throw new IllegalStateException("CheckIn wegen Doppelbelegung des Zimmers abgelehnt!");
        }

    }

}
