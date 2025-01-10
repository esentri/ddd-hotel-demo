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

import com.esentri.rezeption.core.domain.buchung.Buchungseingang;
import com.esentri.rezeption.core.domain.buchung.CheckIn;
import com.esentri.rezeption.core.domain.buchung.CheckOut;
import com.esentri.rezeption.core.domain.rechnung.RechnungsErstellung;
import com.esentri.rezeption.core.outport.Buchungen;
import com.esentri.rezeption.core.outport.DomainEventPublisher;
import com.esentri.rezeption.core.outport.Rechnungen;
import com.esentri.rezeption.core.outport.ServiceLeistungen;
import com.esentri.rezeption.core.outport.ZimmerAuslastungen;
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
     * @param buchungen Repository für Buchungen.
     * @param serviceLeistungen Repository für Serviceleistungsdetails.
     * @param domainEventPublisher Objekt zur Veröffentlichung von Domain-Ereignissen.
     * @return Die Instanz des konfigurierten RechnungsErstellung.
     */
    @Bean
    public RechnungsErstellung rechnungsErstellungsService(
            Rechnungen rechnungen,
            Buchungen buchungen,
            ServiceLeistungen serviceLeistungen,
            DomainEventPublisher domainEventPublisher
    ){
        return new RechnungsErstellung(
                rechnungen,
                buchungen,
                serviceLeistungen,
                domainEventPublisher
        );
    }

    /**
     * Erstellt und konfiguriert eine Instanz des CheckIn.
     *
     * @param buchungen Repository für Buchungsdetails.
     * @param zimmerVerwaltung Repository für Zimmerdetails.
     * @param domainEventPublisher Objekt zur Veröffentlichung von Domain-Ereignissen.
     * @return Die Instanz des konfigurierten CheckIn.
     */
    @Bean
    public CheckIn checkInService(
            Buchungen buchungen,
            ZimmerVerwaltung zimmerVerwaltung,
            DomainEventPublisher domainEventPublisher
    ){
        return new CheckIn(
                buchungen,
                zimmerVerwaltung,
                domainEventPublisher
        );
    }

    /**
     * Erstellt und konfiguriert eine Instanz des CheckOut.
     *
     * @param buchungen Repository für Buchungsdetails.
     * @param rechnungen Repository für Rechnungsdetails.
     * @param serviceLeistungen Repository für Serviceleistungsdetails.
     * @param domainEventPublisher Objekt zur Veröffentlichung von Domain-Ereignissen.
     * @return Die Instanz des konfigurierten CheckOut.
     */
    @Bean
    public CheckOut checkOutService(
            Buchungen buchungen,
            Rechnungen rechnungen,
            ServiceLeistungen serviceLeistungen,
            DomainEventPublisher domainEventPublisher
    ){
        return new CheckOut(
                buchungen,
                rechnungen,
                serviceLeistungen,
                domainEventPublisher
        );
    }

    /**
     * Erstellt und konfiguriert eine Instanz des Buchungseingang.
     *
     * @param buchungen Repository für Buchungsdetails.
     * @param zimmerauslastungen QueryHandler für Zimmerauslastung.
     * @param domainEventPublisher Objekt zur Veröffentlichung von Domain-Ereignissen.
     * @return Die Instanz des konfigurierten Buchungseingang.
     */
    @Bean
    public Buchungseingang buchungsEingangService(
            Buchungen buchungen,
            ZimmerAuslastungen zimmerauslastungen,
            DomainEventPublisher domainEventPublisher
    ){
        return new Buchungseingang(
                buchungen,
                zimmerauslastungen,
                domainEventPublisher
        );
    }

}