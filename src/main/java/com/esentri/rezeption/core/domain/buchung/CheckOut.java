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

import com.esentri.rezeption.core.domain.rechnung.Rechnung;
import com.esentri.rezeption.core.outport.Buchungen;
import com.esentri.rezeption.core.outport.DomainEventPublisher;
import com.esentri.rezeption.core.outport.Rechnungen;
import com.esentri.rezeption.core.outport.ServiceLeistungen;
import io.domainlifecycles.domain.types.DomainService;
import io.domainlifecycles.domain.types.Publishes;
import lombok.RequiredArgsConstructor;

/**
 * DomainService zur Unterstützung der Abfertigung von Auscheck-Ooperationen.
 * Der Service nutzt eine Zusammenarbeit von Repositories und Domain-Klassen, um ein vollumfängliches Auschecken zu ermöglichen.
 * Enthält alle notwendigen Repositories und Services zum Validieren und Durchführen einer Check-out-Operation.
 *
 * @author Mario Herb
 */
@RequiredArgsConstructor
public class CheckOut implements DomainService {

    private final Buchungen buchungen;

    private final Rechnungen rechnungen;

    private final ServiceLeistungen serviceLeistungen;

    private final DomainEventPublisher domainEventPublisher;

    /**
     * Behandelt den Auscheckvorgang für eine gegebene BuchungsNummer.
     *
     * Wirft eine IllegalStateException, wenn die Zimmerabrechnung noch nicht erfolgt ist oder Serviceleistungen noch nicht abgerechnet sind.
     * Veröffentlicht ein BuchungAusgecheckt Ereignis.
     *
     * @param checkeAus Command für den Checkout
     * @return Die BuchungsNummer der Auscheck-Operation.
     * @throws IllegalStateException wenn die Zimmerabrechnung noch nicht erfolgt ist oder wenn Serviceleistungen noch nicht abgerechnet sind.
     */
    @Publishes(domainEventTypes = BuchungAusgecheckt.class)
    public Buchung.BuchungsNummer handle(CheckeAus checkeAus){

        var rechnungen = this.rechnungen.findByBuchungsNummer(checkeAus.buchungsNummer());

        var zimmerRechnungExistiert = rechnungen.stream().anyMatch(Rechnung::beinhaltetZimmerAbrechnung);

        if(!zimmerRechnungExistiert){
            throw new IllegalStateException("Vor dem Auschecken muss die Zimmerabrechnung erfolgen!");
        }

        var offeneServiceLeistungenExistieren = serviceLeistungen.find(checkeAus.buchungsNummer())
                .stream().anyMatch(sl -> sl.getAbgerechnetPer() == null);

        if(offeneServiceLeistungenExistieren){
            throw new IllegalStateException("Es wurden nicht alle ServiceLeistungen der Buchung abgerechnet!");
        }

        var buchung = buchungen.findById(checkeAus.buchungsNummer())
            .map( r ->
                    buchungen.update(r.checkeAus()))
            .orElseThrow();

        domainEventPublisher.publish(new BuchungAusgecheckt(buchung.getBuchungsNummer(), buchung.getCheckOutAm(), buchung.getZimmerNummer()));

        return  buchung.id();
    }

}
