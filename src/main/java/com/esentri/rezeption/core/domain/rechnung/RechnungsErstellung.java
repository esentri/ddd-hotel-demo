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

package com.esentri.rezeption.core.domain.rechnung;

import com.esentri.rezeption.core.domain.Adresse;
import com.esentri.rezeption.core.domain.Preis;
import com.esentri.rezeption.core.domain.serviceleistung.ServiceLeistung;
import com.esentri.rezeption.core.outport.Buchungen;
import com.esentri.rezeption.core.outport.DomainEventPublisher;
import com.esentri.rezeption.core.outport.Rechnungen;
import com.esentri.rezeption.core.outport.ServiceLeistungen;
import io.domainlifecycles.domain.types.DomainService;
import io.domainlifecycles.domain.types.Publishes;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Stellt einen Service dar, der zum Erstellen von Rechnungen für Buchungen und
 * Serviceleistungen verwendet wird. Abhängigkeiten auf Repositories und EventPublisher
 * sind notwendig.
 *
 * @author Mario Herb
 */
@RequiredArgsConstructor
public class RechnungsErstellung implements DomainService {

    private final Rechnungen rechnungen;

    private final Buchungen buchungen;

    private final ServiceLeistungen serviceLeistungen;

    private final DomainEventPublisher domainEventPublisher;

    /**
     * Verarbeite die Anfrage zur Erstellung einer Rechnung für eine Buchung.
     * Die Anforderung enthält die BuchungsNummer und eventuelle Serviceleistungen.
     * Nach dem Erstellen der Rechnung wird ein RechnungErstellt-Ereignis veröffentlicht.
     *
     * @param erstelleRechnungFuerBuchung die Anforderung zur Erstellung einer Rechnung für eine Buchung.
     * @return die ID der erstellten Rechnung.
     */
    @Publishes(domainEventTypes = RechnungErstellt.class)
    public Rechnung.Id handle(ErstelleRechnungFuerBuchung erstelleRechnungFuerBuchung){
        var buchung = buchungen.findById(erstelleRechnungFuerBuchung.buchungsNummer()).orElseThrow();
        var serviceLeistungen = this.serviceLeistungen.find(erstelleRechnungFuerBuchung.buchungsNummer())
                .stream().filter(sl -> erstelleRechnungFuerBuchung.serviceLeistungen().contains(sl.id()))
                .toList();

        var neueRechnung = neueRechnungFuerBuchung(
                erstelleRechnungFuerBuchung,
                buchung.getAngebotenerZimmerpreis(),
                erstelleRechnungFuerBuchung.rechnungsAdresse(),
                serviceLeistungen
        );

        rechnungen.insert(neueRechnung);
        domainEventPublisher.publish(new RechnungErstellt(
                    neueRechnung.id(),
                    neueRechnung.getBuchungsNummer(),
                    neueRechnung.getGesamtNetto(),
                    serviceLeistungen.stream().map(ServiceLeistung::id).toList()
                )
        );
        return neueRechnung.id();
    }

    /**
     * Verarbeitet die Anforderung zum Erstellen einer Rechnung für Serviceleistungen.
     * Die Anforderung enthält die BuchungsNummer und Serviceleistungen.
     * Nach der Erstellung der Rechnung wird ein RechnungErstellt-Ereignis veröffentlicht.
     *
     * @param erstelleServiceRechnung die Anforderung zum Erstellen einer Rechnung für eine Serviceleistung.
     * @return die ID der erstellten Rechnung.
     */
    @Publishes(domainEventTypes = RechnungErstellt.class)
    public Rechnung.Id handle(ErstelleServiceRechnung erstelleServiceRechnung){
        var serviceLeistungen = this.serviceLeistungen.find(erstelleServiceRechnung.buchungsNummer())
                .stream().filter(sl -> erstelleServiceRechnung.serviceLeistungen().contains(sl.id()))
                .toList();
        var neueRechnung = neueServiceRechnung(
                erstelleServiceRechnung,
                erstelleServiceRechnung.rechnungsAdresse(),
                serviceLeistungen
        );

        rechnungen.insert(neueRechnung);
        domainEventPublisher.publish(new RechnungErstellt(
                neueRechnung.id(),
                neueRechnung.getBuchungsNummer(),
                neueRechnung.getGesamtNetto(),
                serviceLeistungen.stream().map(ServiceLeistung::id).toList()
                ));
        return neueRechnung.id();
    }

    private static Rechnung neueRechnungFuerBuchung(ErstelleRechnungFuerBuchung erstelleRechnungFuerBuchung,
                                                         Preis zimmerPreis,
                                                         Adresse rechnungsAdresse,
                                                         List<ServiceLeistung> serviceLeistungen){
        var rechnung = new Rechnung(
                new Rechnung.Id(UUID.randomUUID()),
                erstelleRechnungFuerBuchung.buchungsNummer(),
                zimmerPreis,
                LocalDateTime.now(),
                rechnungsAdresse
        );
        serviceLeistungen.forEach(sl -> rechnung.addRechnungsPosition(sl.getNettoPreis(), sl.getBeschreibung(), sl.id()));
        return rechnung;
    }

    private static Rechnung neueServiceRechnung(ErstelleServiceRechnung erstelleServiceRechnung,
                                                Adresse rechnungsAdresse,
                                                List<ServiceLeistung> serviceLeistungen
    ){
        var rechnung = new Rechnung(
                new Rechnung.Id(UUID.randomUUID()),
                erstelleServiceRechnung.buchungsNummer(),
                null,
                LocalDateTime.now(),
                rechnungsAdresse
        );
        serviceLeistungen.forEach(sl -> rechnung.addRechnungsPosition(sl.getNettoPreis(), sl.getBeschreibung(), sl.id()));
        return rechnung;
    }
}
