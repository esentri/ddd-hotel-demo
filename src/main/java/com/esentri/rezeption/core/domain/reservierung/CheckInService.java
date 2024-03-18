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

package com.esentri.rezeption.core.domain.reservierung;

import com.esentri.rezeption.core.domain.zimmer.BelegungTyp;
import com.esentri.rezeption.core.outport.DomainEventPublisher;
import com.esentri.rezeption.core.outport.ReservierungRepository;
import com.esentri.rezeption.core.outport.ZimmerRepository;
import lombok.RequiredArgsConstructor;
import nitrox.dlc.domain.types.DomainService;
import nitrox.dlc.domain.types.Publishes;

import java.time.LocalDate;

/**
 * Dieser Service übernimmt die Verarbeitung der Check-In-Operationen.
 * Er integriert mehrere Repositories, um relevante Daten abzurufen und zu aktualisieren.
 * Er verwendet auch einen DomainEventPublisher, um relevante Ereignisse zu veröffentlichen.
 *
 * @author Mario Herb
 */
@RequiredArgsConstructor
public class CheckInService implements DomainService {

    private final ReservierungRepository reservierungRepository;

    private final ZimmerRepository zimmerRepository;

    private final DomainEventPublisher domainEventPublisher;

    /**
     * Verarbeitet den CheckeEin Command für eine Reservierung.
     * Dabei wird zuerst die Reservierung anhand der Reservierungsnummer abgerufen und aktualisiert. Anschließend wird das Zimmer anhand der Zimmernummer ermittelt.
     * Schließlich wird das Check-In-Datum und das Check-Out-Datum berechnet und festgelegt.
     * Wenn das Zimmer für den gewünschten Zeitraum zur Verfügung steht, wird ein Check-In durchgeführt und das entsprechende Ereignis veröffentlicht.
     * Ansonsten wird eine IllegalStateException geworfen.
     *
     * @param checkeEin Informationen zum Check-In-Vorgang.
     * @return Reservierungsnummer der erfolgreich eingecheckten Reservierung.
     * @throws IllegalStateException Wenn das Zimmer zum gewünschten Zeitpunkt bereits belegt ist.
     */
    @Publishes(domainEventTypes = ReservierungEingecheckt.class)
    public Reservierung.ReservierungsNummer handle(CheckeEin checkeEin){
        var reservierung = reservierungRepository.findById(checkeEin.reservierungsNummer()).orElseThrow();
        reservierungRepository.update(reservierung.handle(checkeEin));

        var zimmer = zimmerRepository.findById(checkeEin.zimmerNummer()).orElseThrow();

        var checkInAm = LocalDate.now();
        var checkOutAm = LocalDate.now().plusDays(checkeEin.geplanteAnzahlNaechte());

        if(zimmer.neueBelegung(LocalDate.now(), checkOutAm, BelegungTyp.GAST)){
            zimmerRepository.update(zimmer);
            domainEventPublisher.publish(
                    new ReservierungEingecheckt(
                            reservierung.id(),
                            checkeEin.zimmerNummer(),
                            checkInAm,
                            checkOutAm
                    )
            );
            return  reservierung.id();
        }else{
            throw new IllegalStateException("CheckIn wegen Doppelbelegung des Zimmers abgelehnt!");
        }

    }

}
