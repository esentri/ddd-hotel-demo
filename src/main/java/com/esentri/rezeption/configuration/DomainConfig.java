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

package com.esentri.rezeption.configuration;

import com.esentri.rezeption.core.domain.rechnung.RechnungsErstellung;
import com.esentri.rezeption.core.domain.reservierung.CheckIn;
import com.esentri.rezeption.core.domain.reservierung.CheckOut;
import com.esentri.rezeption.core.domain.reservierung.Reservierungseingang;
import com.esentri.rezeption.core.outport.DomainEventPublisher;
import com.esentri.rezeption.core.outport.Rechnungen;
import com.esentri.rezeption.core.outport.Reservierungen;
import com.esentri.rezeption.core.outport.ServiceLeistungen;
import com.esentri.rezeption.core.outport.ZimmerVerwaltung;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfigurationsklasse ist für die Einrichtung der Bean-Definitionen verantwortlich.
 * Diese Klasse stellt die Servicebeans zur Verfügung, die von der Anwendung benötigt werden.
 *
 * @author Mario Herb
 */
@Configuration
public class DomainConfig {

    /**
     * Erstellt und konfiguriert eine Instanz des RechnungsErstellungsServices.
     *
     * @param rechnungen Repository für Rechnungsdetails.
     * @param reservierungen Repository für Reservierungsdetails.
     * @param serviceLeistungen Repository für Serviceleistungsdetails.
     * @param domainEventPublisher Objekt zur Veröffentlichung von Domain-Ereignissen.
     * @return Die Instanz des konfigurierten RechnungsErstellung.
     */
    @Bean
    public RechnungsErstellung rechnungsErstellungsService(
            Rechnungen rechnungen,
            Reservierungen reservierungen,
            ServiceLeistungen serviceLeistungen,
            DomainEventPublisher domainEventPublisher
    ){
        return new RechnungsErstellung(
                rechnungen,
                reservierungen,
                serviceLeistungen,
                domainEventPublisher
        );
    }

    /**
     * Erstellt und konfiguriert eine Instanz des CheckIn.
     *
     * @param reservierungen Repository für Reservierungsdetails.
     * @param zimmerVerwaltung Repository für Zimmerdetails.
     * @param domainEventPublisher Objekt zur Veröffentlichung von Domain-Ereignissen.
     * @return Die Instanz des konfigurierten CheckIn.
     */
    @Bean
    public CheckIn checkInService(
            Reservierungen reservierungen,
            ZimmerVerwaltung zimmerVerwaltung,
            DomainEventPublisher domainEventPublisher
    ){
        return new CheckIn(
                reservierungen,
                zimmerVerwaltung,
                domainEventPublisher
        );
    }

    /**
     * Erstellt und konfiguriert eine Instanz des CheckOut.
     *
     * @param reservierungen Repository für Reservierungsdetails.
     * @param rechnungen Repository für Rechnungsdetails.
     * @param serviceLeistungen Repository für Serviceleistungsdetails.
     * @param domainEventPublisher Objekt zur Veröffentlichung von Domain-Ereignissen.
     * @return Die Instanz des konfigurierten CheckOut.
     */
    @Bean
    public CheckOut checkOutService(
            Reservierungen reservierungen,
            Rechnungen rechnungen,
            ServiceLeistungen serviceLeistungen,
            DomainEventPublisher domainEventPublisher
    ){
        return new CheckOut(
                reservierungen,
                rechnungen,
                serviceLeistungen,
                domainEventPublisher
        );
    }

    /**
     * Erstellt und konfiguriert eine Instanz des Reservierungseingang.
     *
     * @param reservierungen Repository für Reservierungsdetails.
     * @param zimmerVerwaltung Repository für Zimmerdetails.
     * @param domainEventPublisher Objekt zur Veröffentlichung von Domain-Ereignissen.
     * @return Die Instanz des konfigurierten Reservierungseingang.
     */
    @Bean
    public Reservierungseingang reservierungsEingangService(
            Reservierungen reservierungen,
            ZimmerVerwaltung zimmerVerwaltung,
            DomainEventPublisher domainEventPublisher
    ){
        return new Reservierungseingang(
                reservierungen,
                zimmerVerwaltung,
                domainEventPublisher
        );
    }

}